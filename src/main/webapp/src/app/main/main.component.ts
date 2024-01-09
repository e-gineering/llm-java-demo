import { Component, OnInit } from '@angular/core';
import { MatCardModule } from "@angular/material/card";
import { CommonModule } from "@angular/common";
import { MatInputModule } from "@angular/material/input";
import { MatButtonModule } from "@angular/material/button";
import { FormControl, FormGroup, ReactiveFormsModule } from "@angular/forms";
import { ApiService } from "../services/api.service";
import { MatDividerModule } from "@angular/material/divider";
import { RxStompService } from "../services/rx-stomp.service";
import { IMessage } from "@stomp/rx-stomp";
import { MatExpansionModule } from "@angular/material/expansion";

@Component({
  selector: 'app-main',
  standalone: true,
  imports: [
    CommonModule,
    MatButtonModule,
    MatCardModule,
    MatInputModule,
    ReactiveFormsModule,
    MatDividerModule,
    MatExpansionModule
  ],
  templateUrl: './main.component.html',
  styleUrl: './main.component.scss'
})
export class MainComponent implements OnInit {

  messageForm = new FormGroup({
    message: new FormControl()
  })

  responseWs: { prompt: string, files: string[] } = {prompt: '', files: []};
  responseWsStreaming: { response: string } = {response: ''};
  responseBasic: { response: string } = {response: ''};
  responseFaq: { response: string } = {response: ''};
  responseDocs: { response: string } = {response: ''};
  responseData: { response: string } = {response: ''};

  constructor(private rxStompService: RxStompService, private apiService: ApiService) {}

  ngOnInit() {
    this.rxStompService.watch('/topic/llmResponse').subscribe((message: IMessage) => {
      this.responseWs.prompt = JSON.parse(message.body).prompt;
      this.responseWs.files = JSON.parse(message.body).files;
    });

    this.rxStompService.watch('/topic/llmStreamingResponse').subscribe((message: IMessage) => {
      this.responseWsStreaming.response = this.responseWsStreaming.response ? this.responseWsStreaming.response + JSON.parse(message.body).response : JSON.parse(message.body).response;
    });
  }

  submitMessageWs() {
    this.responseWs.prompt = '';
    this.responseWs.files = [];
    this.responseWsStreaming.response = '';
    this.rxStompService.publish({ destination: '/app/llmStreamingRequest', body: JSON.stringify(this.messageForm.getRawValue()) })
  }

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
