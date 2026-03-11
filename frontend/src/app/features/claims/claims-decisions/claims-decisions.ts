import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ClaimsService } from '../services/claims.service';
import { toSignal } from '@angular/core/rxjs-interop';
import { map } from 'rxjs';

@Component({
    selector: 'app-claims-decisions',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './claims-decisions.html'
})
export class ClaimsDecisions {
    private service = inject(ClaimsService);

    claims = toSignal(
        this.service.getAllClaims().pipe(
            map(data => data.filter(c => c.status !== 'PENDING' && c.status !== 'UNDER_INVESTIGATION'))
        ),
        { initialValue: [] as any[] }
    );

    statusClass(status: string) {
        return {
            'bg-emerald-500/20 text-emerald-300 border border-emerald-500/40': status === 'APPROVED',
            'bg-red-500/20 text-red-300 border border-red-500/40': status === 'REJECTED',
            'bg-cyan-500/20 text-cyan-300 border border-cyan-500/40': status === 'SETTLED',
        };
    }

    formatCurrency(val: number): string {
        if (!val) return '₹0';
        return '₹' + val.toLocaleString('en-IN', { maximumFractionDigits: 2 });
    }
}