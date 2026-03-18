import { Routes } from '@angular/router';
import { UnderwriterDashboard } from './dashboard/underwriter-dashboard';
import { ReviewQueue } from './review-queue/review-queue';
import { Decisions } from './decisions/decisions';

export const UNDERWRITER_ROUTES: Routes = [

    {
        path: '',
        component: UnderwriterDashboard,
        children: [

            {
                path: '',
                redirectTo: 'queue',
                pathMatch: 'full'
            },

            {
                path: 'queue',
                component: ReviewQueue
            },

            {
                path: 'decisions',
                component: Decisions
            }

        ]
    }

];