import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { toSignal } from '@angular/core/rxjs-interop';
import { catchError, of, BehaviorSubject, switchMap } from 'rxjs';
import { AdminApiService } from '../services/admin.service';

@Component({
  selector: 'app-claims',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './claims.html'
})
export class Claims {

  private adminApi = inject(AdminApiService);
  private refresh$ = new BehaviorSubject<void>(undefined);

  claimsList = toSignal(
    this.refresh$.pipe(
      switchMap(() => this.adminApi.getClaims().pipe(catchError(() => of([]))))
    ),
    { initialValue: [] }
  );

  officers = toSignal(
    this.adminApi.getClaimsOfficers().pipe(catchError(() => of([]))),
    { initialValue: [] }
  );

  assignOfficer(claim: any) {
    if (!claim.selectedOfficerId) {
      alert('Please select an officer first');
      return;
    }
    this.adminApi.assignClaimsOfficer(claim.id, parseInt(claim.selectedOfficerId, 10)).subscribe({
      next: () => {
        alert('Officer assigned successfully!');
        this.refresh$.next(); // Instantly refresh
      },
      error: (err) => { alert('Failed to assign officer.'); console.error(err); }
    });
  }
}