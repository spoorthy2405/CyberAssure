import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { toSignal } from '@angular/core/rxjs-interop';
import { catchError, of } from 'rxjs';
import { AdminApiService } from '../services/admin.service';

@Component({
  selector: 'app-analytics',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './analytics.html'
})
export class Analytics {

  private adminApi = inject(AdminApiService);

  analyticsData = toSignal(
    this.adminApi.getAnalytics().pipe(catchError(() => of({}))),
    { initialValue: {} }
  );
}