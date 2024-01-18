import { Component } from '@angular/core';
import { LoaderService } from "../services/loader.service";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { NgIf } from "@angular/common";

@Component({
  selector: 'app-spinner',
  standalone: true,
    imports: [
        MatProgressSpinnerModule,
        NgIf
    ],
  templateUrl: './spinner.component.html',
  styleUrl: './spinner.component.scss'
})
export class SpinnerComponent {

  constructor(public loader: LoaderService) { }
}
