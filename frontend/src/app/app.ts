import { Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { LoadingService } from './core/services/loading.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, CommonModule],
  template: `
    <!-- GLOBAL HTTP LOADING BAR -->
    <div *ngIf="loadingService.isLoading()" class="fixed top-0 left-0 w-full h-1 z-[9999]">
      <div class="h-full bg-blue-500 animate-pulse relative overflow-hidden">
        <div class="absolute inset-0 bg-white/30 skew-x-[-20deg] animate-[slide_1.5s_infinite]"></div>
      </div>
    </div>
    <router-outlet />
  `,
  styles: [`
    @keyframes slide {
      0% { transform: translateX(-100%) skewX(-20deg); }
      100% { transform: translateX(200%) skewX(-20deg); }
    }
  `]
})
export class App {
  loadingService = inject(LoadingService);
}