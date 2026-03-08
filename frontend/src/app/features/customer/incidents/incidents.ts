import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { CustomerService } from '../services/customer.service';

@Component({
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './incidents.html'
})
export class Incidents {

    data: any = {};

    constructor(private service: CustomerService) { }

    submit() {

        this.service.reportIncident(this.data).subscribe(() => {

            alert("Incident Reported");

        });

    }

}