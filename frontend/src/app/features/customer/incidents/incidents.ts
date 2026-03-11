import { Component, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink, Router, NavigationEnd } from '@angular/router';
import { CustomerService } from '../services/customer.service';
import { toSignal } from '@angular/core/rxjs-interop';
import { catchError, filter, startWith, switchMap, tap } from 'rxjs/operators';
import { of, BehaviorSubject } from 'rxjs';

@Component({
    standalone: true,
    imports: [CommonModule, FormsModule, RouterLink],
    templateUrl: './incidents.html'
})
export class Incidents {
    private service = inject(CustomerService);
    private router = inject(Router);

    private manualRefresh$ = new BehaviorSubject<void>(undefined);
    private refresh$ = this.router.events.pipe(
        filter(event => event instanceof NavigationEnd),
        startWith(null),
        switchMap(() => this.manualRefresh$)
    );

    activeTab = signal<'list' | 'report'>('list');

    incidentsLoading = signal(true);
    incidents = toSignal(
        this.refresh$.pipe(
            tap(() => this.incidentsLoading.set(true)),
            switchMap(() => this.service.getMyIncidents().pipe(
                catchError(() => of([]))
            )),
            tap(() => this.incidentsLoading.set(false))
        ),
        { initialValue: [] as any[] }
    );

    subsLoading = signal(true);
    subscriptions = toSignal(
        this.refresh$.pipe(
            tap(() => this.subsLoading.set(true)),
            switchMap(() => this.service.getSubscriptions().pipe(
                catchError(() => of([]))
            )),
            tap((subs) => {
                const activeSubs = subs.filter((s: any) => s.status === 'APPROVED' || s.status === 'ACTIVE');
                if (activeSubs.length > 0 && !this.formData().subscriptionId) {
                    this.updateFormData('subscriptionId', activeSubs[0].id);
                }
                
                this.subsLoading.set(false);
            })
        ),
        { initialValue: [] as any[] }
    );

    activeSubscriptions = computed(() => {
        return this.subscriptions().filter((s: any) => s.status === 'APPROVED' || s.status === 'ACTIVE');
    });

    // Report form
    formData = signal({ incidentType: '', description: '', estimatedLossAmount: null as number | null, subscriptionId: null as number | null });
    selectedFiles = signal<File[]>([]);
    formLoading = signal(false);
    formError = signal('');
    formSuccess = signal('');

    updateFormData(field: string, value: any) {
        this.formData.update(f => ({ ...f, [field]: value }));
    }

    onFileSelected(event: any) {
        if (event.target.files.length > 0) {
            this.selectedFiles.set(Array.from(event.target.files));
        }
    }

    removeFile(index: number) { 
        this.selectedFiles.update(curr => {
            const copy = [...curr];
            copy.splice(index, 1);
            return copy;
        });
    }

    submitReport() {
        this.formError.set('');
        this.formSuccess.set('');
        const data = this.formData();
        
        if (!data.subscriptionId) { this.formError.set('Please select an active policy.'); return; }
        if (!data.incidentType) { this.formError.set('Please select an incident type.'); return; }
        if (!data.description) { this.formError.set('Please describe the incident.'); return; }
        if (!data.estimatedLossAmount) { this.formError.set('Please enter an estimated loss amount.'); return; }

        this.formLoading.set(true);
        const fd = new FormData();
        const payload = {
            incidentType: data.incidentType,
            description: data.description,
            estimatedLossAmount: data.estimatedLossAmount,
            subscriptionId: data.subscriptionId
        };
        fd.append('data', new Blob([JSON.stringify(payload)], { type: 'application/json' }));
        
        this.selectedFiles().forEach(f => fd.append('files', f));

        this.service.reportIncident(fd).subscribe({
            next: () => {
                this.formLoading.set(false);
                this.formSuccess.set('Incident reported successfully! Our claims team will review it shortly.');
                this.formData.set({ incidentType: '', description: '', estimatedLossAmount: null, subscriptionId: this.activeSubscriptions()[0]?.id });
                this.selectedFiles.set([]);
                
                // Force data refetch for incidents
                this.manualRefresh$.next();
                
                // Redirect immediately
                this.activeTab.set('list');
            },
            error: (err) => {
                this.formLoading.set(false);
                this.formError.set(err.error?.message || 'Failed to report incident. Ensure you have an active policy.');
            }
        });
    }

    getStatusColor(status: string) {
        const map: any = { 'REPORTED': 'bg-amber-500/10 text-amber-400 border-amber-500/30', 'UNDER_INVESTIGATION': 'bg-blue-500/10 text-blue-400 border-blue-500/30', 'CLOSED': 'bg-emerald-500/10 text-emerald-400 border-emerald-500/30' };
        return map[status] || 'bg-amber-500/10 text-amber-400 border-amber-500/30';
    }
}