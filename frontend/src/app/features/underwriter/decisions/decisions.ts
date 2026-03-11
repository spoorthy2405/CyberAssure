import { Component, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { toSignal } from '@angular/core/rxjs-interop';
import { catchError, of } from 'rxjs';
import { UnderwriterService } from '../services/underwriter.service';

@Component({
  selector: 'app-decisions',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './decisions.html'
})
export class Decisions {

  private service = inject(UnderwriterService);

  private allSubs = toSignal(
    this.service.getMyAssignedSubscriptions().pipe(catchError(() => of([]))),
    { initialValue: [] as any[] }
  );

  // Show only APPROVED or REJECTED — computed auto-updates when allSubs changes
  decisions = computed(() =>
    this.allSubs().filter((s: any) => s.status !== 'PENDING')
  );

  loading = computed(() => this.allSubs().length === 0);
}