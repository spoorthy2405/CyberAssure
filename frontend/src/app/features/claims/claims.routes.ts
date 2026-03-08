import { Routes } from '@angular/router';

export const CLAIMS_ROUTES: Routes = [

    {
        path: 'dashboard',
        loadComponent: () => import('./claims-dashboard/claims-dashboard')
            .then(m => m.ClaimsDashboard)
    },

    {
        path: 'queue',
        loadComponent: () => import('./claims-queue/claims-queue')
            .then(m => m.ClaimsQueue)
    },

    {
        path: 'decisions',
        loadComponent: () => import('./claims-decisions/claims-decisions')
            .then(m => m.ClaimsDecisions)
    },

    {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
    }

];