import { Component } from '@angular/core';
import { MatCardModule } from "@angular/material/card";
import { CommonModule } from "@angular/common";
import { MatInputModule } from "@angular/material/input";
import { MatButtonModule } from "@angular/material/button";
import { FormControl, FormGroup, ReactiveFormsModule } from "@angular/forms";
import { ApiService } from "../services/api.service";
import { MatDividerModule } from "@angular/material/divider";

@Component({
  selector: 'app-main',
  standalone: true,
  imports: [
    CommonModule,
    MatButtonModule,
    MatCardModule,
    MatInputModule,
    ReactiveFormsModule,
    MatDividerModule
  ],
  templateUrl: './main.component.html',
  styleUrl: './main.component.scss'
})
export class MainComponent {

  messageForm = new FormGroup({
    message: new FormControl()
  })

  responseBasic: { response: string } = {response: ''};
  responseFaq: { response: string } = {response: ''};
  responseDocs: { response: string } = {response: ''};
  responseData: { response: string } = {response: ''};

  constructor(private apiService: ApiService) {}

  submitMessageBasic() {
    this.apiService.postMessageBasic(this.messageForm).subscribe({
      next: value => {
        this.responseBasic = value;
        this.messageForm.reset();
      }
    });
  }

  submitMessageFaq() {
    this.apiService.postMessageFaq(this.messageForm).subscribe({
      next: value => {
        this.responseFaq = value;
        this.messageForm.reset();
      }
    });
  }

  submitMessageDocs() {
    this.apiService.postMessageDocs(this.messageForm).subscribe({
      next: value => {
        this.responseDocs = value;
        this.messageForm.reset();
      }
    });
  }

  submitMessageData() {
    this.apiService.postMessageData(this.messageForm).subscribe({
      next: value => {
        this.responseData = value;
        this.messageForm.reset();
      },
      complete: ()=> {

      }
    });
  }}
