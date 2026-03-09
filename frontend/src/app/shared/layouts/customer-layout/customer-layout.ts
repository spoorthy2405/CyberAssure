import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CustomerSidebar } from '../customer-sidebar/customer-sidebar';
import { Header } from '../header/header';

@Component({
    selector: 'app-customer-layout',
    standalone: true,
    imports: [RouterOutlet, CustomerSidebar, Header],
    templateUrl: './customer-layout.html'
})
export class CustomerLayout { }
