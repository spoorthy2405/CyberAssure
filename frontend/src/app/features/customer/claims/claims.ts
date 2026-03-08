import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CustomerService } from '../services/customer.service';

@Component({
    standalone: true,
    imports: [CommonModule],
    templateUrl: './claims.html'
})
export class Claims implements OnInit {

    claims: any[] = [];

    constructor(private service: CustomerService) { }

    ngOnInit() {

        this.service.getClaims().subscribe(data => {

            this.claims = data;

        });

    }

}