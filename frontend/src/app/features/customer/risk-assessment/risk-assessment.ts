import { Component } from '@angular/core';

import { FormsModule } from '@angular/forms';
import { CustomerService } from '../services/customer.service';

@Component({
    standalone: true,
    imports: [FormsModule],
    templateUrl: './risk-assessment.html'
})
export class RiskAssessment {

    data: any = {};

    constructor(private service: CustomerService) { }

    submit() {
        this.service.createRisk(this.data).subscribe(() => {
            alert("Risk Assessment Submitted");
        });
    }

}