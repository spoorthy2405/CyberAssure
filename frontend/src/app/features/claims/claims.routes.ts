import { Routes } from '@angular/router';
import { ClaimsDashboard } from './claims-dashboard/claims-dashboard';
import { ClaimsQueue } from './claims-queue/claims-queue';
import { ClaimsDecisions } from './claims-decisions/claims-decisions';

export const CLAIMS_ROUTES: Routes = [
    {
        path: '',
        component: ClaimsDashboard,
        children: [
            {
                path: '',
                redirectTo: 'queue',
                pathMatch: 'full'
            },
            {
                path: 'queue',
                component: ClaimsQueue
            },
            {
                path: 'decisions',
                component: ClaimsDecisions
            }
        ]
    }
];