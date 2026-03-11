import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { toSignal } from '@angular/core/rxjs-interop';
import { catchError, of } from 'rxjs';
import { AdminApiService } from '../services/admin.service';

@Component({
  selector: 'app-risk-monitoring',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './risk-monitoring.html'
})
export class RiskMonitoring {

  private adminApi = inject(AdminApiService);

  riskData = toSignal(
    this.adminApi.getRiskMonitoring().pipe(catchError(() => of({}))),
    { initialValue: {} }
  );

  // Static alerts (can later come from backend)
  alerts = [
    {
      type: 'Critical',
      source: 'Policy System',
      description: 'Multiple suspicious claims detected',
      time: '10 mins ago'
    },
    {
      type: 'Warning',
      source: 'Risk Engine',
      description: 'Risk score increased significantly',
      time: '2 hours ago'
    },
    {
      type: 'Info',
      source: 'System',
      description: 'Automated risk evaluation completed',
      time: '5 hours ago'
    }
  ];
}
