import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminApiService, User } from '../services/admin.service';

@Component({
    selector: 'app-customers',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './customers.html'
})
export class Customers implements OnInit {

    customersList: User[] = [];
    filteredCustomers: User[] = [];

    searchText: string = '';

    page = 1;
    pageSize = 5;

    constructor(private adminApi: AdminApiService) { }

    ngOnInit() {
        this.adminApi.getCustomers().subscribe({
            next: (data) => {
                this.customersList = data;
                this.filteredCustomers = data;
            }
        });
    }

    search() {
        const text = this.searchText.toLowerCase();

        this.filteredCustomers = this.customersList.filter(c =>
            c.fullName.toLowerCase().includes(text) ||
            c.email.toLowerCase().includes(text)
        );

        this.page = 1;
    }

    get paginatedCustomers() {
        const start = (this.page - 1) * this.pageSize;
        return this.filteredCustomers.slice(start, start + this.pageSize);
    }

    nextPage() {
        if ((this.page * this.pageSize) < this.filteredCustomers.length) {
            this.page++;
        }
    }

    prevPage() {
        if (this.page > 1) {
            this.page--;
        }
    }

}