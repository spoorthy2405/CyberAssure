import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { toSignal } from '@angular/core/rxjs-interop';
import { catchError, of } from 'rxjs';
import { AdminApiService } from '../services/admin.service';

@Component({
  selector: 'app-underwriters',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './underwriters.html'
})
export class Underwriters {

  private adminApi = inject(AdminApiService);

  underwritersList = toSignal(
    this.adminApi.getUnderwriters().pipe(catchError(() => of([]))),
    { initialValue: [] }
  );
}
