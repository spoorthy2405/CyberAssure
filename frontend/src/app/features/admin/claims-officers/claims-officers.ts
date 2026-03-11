import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { toSignal } from '@angular/core/rxjs-interop';
import { catchError, of } from 'rxjs';
import { AdminApiService } from '../services/admin.service';

@Component({
  selector: 'app-claims-officers',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './claims-officers.html'
})
export class ClaimsOfficers {

  private adminApi = inject(AdminApiService);

  officersList = toSignal(
    this.adminApi.getClaimsOfficers().pipe(catchError(() => of([]))),
    { initialValue: [] }
  );
}
