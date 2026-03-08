import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CustomerService } from '../services/customer.service';

@Component({
    standalone: true,
    imports: [CommonModule, FormsModule],
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