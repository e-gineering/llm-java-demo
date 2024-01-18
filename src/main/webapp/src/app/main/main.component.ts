import { AfterContentInit, AfterViewInit, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
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
import { ChatMessage } from "../models/ChatMessage";
import mermaid from 'mermaid';
import { RouterLink } from "@angular/router";
import { MatTabsModule } from "@angular/material/tabs";
import { BasicComponent } from "../basic/basic.component";
import { FaqComponent } from "../faq/faq.component";
import { DocumentsComponent } from "../documents/documents.component";
import { DataComponent } from "../data/data.component";

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
    MatExpansionModule,
    RouterLink,
    MatTabsModule,
    BasicComponent,
    FaqComponent,
    DocumentsComponent,
    DataComponent
  ],
  templateUrl: './main.component.html',
  styleUrl: './main.component.scss'
})
export class MainComponent implements OnInit, AfterViewInit, AfterContentInit {

  private static chartDefBasic = `sequenceDiagram
    box Grey E-gineering Code
    participant Demo UI
    participant Demo App
    end
    Demo UI ->>+ Demo App: User message
    Demo App ->>+ Chat GPT: Streaming API call
    Chat GPT ->> Demo App: Streaming response
        loop Stream
        Demo App -) Demo App: Receive Tokens
    end
    Chat GPT -)- Demo App: Token stream
    Demo App -)- Demo UI: Token stream`

  messageForm = new FormGroup({
    message: new FormControl()
  })

  responseBasic: { messages: ChatMessage[], files: string[]  } = {messages: [], files: []};
  responseBasicStreaming: { response: string } = {response: ''};
  responseFaq: { messages: ChatMessage[], files: string[]  } = {messages: [], files: []};
  responseFaqStreaming: { response: string } = {response: ''};
  responseDocuments: { messages: ChatMessage[], files: string[] } = {messages: [], files: []};
  responseDocumentsStreaming: { response: string } = {response: ''};
  responseData: { messages: ChatMessage[], files: string[] } = {messages: [], files: []};
  responseDataStreaming: { response: string } = {response: ''};

  @ViewChild('mermaidDivBasic') mermaidDivBasic: ElementRef | undefined;

  constructor(private rxStompService: RxStompService, private apiService: ApiService) {}

  ngOnInit() {
    this.rxStompService.watch('/topic/basic/llmResponse').subscribe((message: IMessage) => {
      this.responseBasic.messages = JSON.parse(message.body).messages;
      this.responseBasic.files = JSON.parse(message.body).files;
    });

    this.rxStompService.watch('/topic/basic/llmStreamingResponse').subscribe((message: IMessage) => {
      this.responseBasicStreaming.response = this.responseBasicStreaming.response ? this.responseBasicStreaming.response + JSON.parse(message.body).response : JSON.parse(message.body).response;
    });

    this.rxStompService.watch('/topic/faq/llmResponse').subscribe((message: IMessage) => {
      this.responseFaq.messages = JSON.parse(message.body).messages;
      this.responseFaq.files = JSON.parse(message.body).files;
    });

    this.rxStompService.watch('/topic/faq/llmStreamingResponse').subscribe((message: IMessage) => {
      this.responseFaqStreaming.response = this.responseFaqStreaming.response ? this.responseFaqStreaming.response + JSON.parse(message.body).response : JSON.parse(message.body).response;
    });

    this.rxStompService.watch('/topic/documents/llmResponse').subscribe((message: IMessage) => {
      this.responseDocuments.messages = JSON.parse(message.body).messages;
      this.responseDocuments.files = JSON.parse(message.body).files;
    });

    this.rxStompService.watch('/topic/documents/llmStreamingResponse').subscribe((message: IMessage) => {
      this.responseDocumentsStreaming.response = this.responseDocumentsStreaming.response ? this.responseDocumentsStreaming.response + JSON.parse(message.body).response : JSON.parse(message.body).response;
    });

    this.rxStompService.watch('/topic/data/llmResponse').subscribe((message: IMessage) => {
      this.responseData.messages = JSON.parse(message.body).messages;
      this.responseData.files = JSON.parse(message.body).files;
    });

    this.rxStompService.watch('/topic/data/llmStreamingResponse').subscribe((message: IMessage) => {
      this.responseDataStreaming.response = this.responseDataStreaming.response ? this.responseDataStreaming.response + JSON.parse(message.body).response : JSON.parse(message.body).response;
    });

    mermaid.initialize({
      securityLevel: 'loose',
      theme: 'dark'
    });

    mermaid.init();
  }

  ngAfterViewInit(): void {
    const element: any = this.mermaidDivBasic?.nativeElement;

    console.log(`##### ${element}`)
    mermaid.render('mermaidDivBasic', MainComponent.chartDefBasic)
        .then(value => element.innerHTML = value.svg);
  }

  ngAfterContentInit(): void {
  }


  submitMessageBasic() {
    this.responseBasic.messages = [];
    this.responseBasic.files = [];
    this.responseBasicStreaming.response = '';
    this.rxStompService.publish({ destination: '/app/basic/llmStreamingRequest', body: JSON.stringify(this.messageForm.getRawValue()) })
  }

  submitMessageFaq() {
    this.responseFaq.messages = [];
    this.responseFaq.files = [];
    this.responseFaqStreaming.response = '';
    this.rxStompService.publish({ destination: '/app/faq/llmStreamingRequest', body: JSON.stringify(this.messageForm.getRawValue()) })
  }

  submitMessageDocuments() {
    this.responseDocuments.messages = [];
    this.responseDocuments.files = [];
    this.responseDocumentsStreaming.response = '';
    this.rxStompService.publish({ destination: '/app/documents/llmStreamingRequest', body: JSON.stringify(this.messageForm.getRawValue()) })
  }

  submitMessageData() {
    this.responseData.messages = [];
    this.responseData.files = [];
    this.responseDataStreaming.response = '';
    this.rxStompService.publish({ destination: '/app/data/llmStreamingRequest', body: JSON.stringify(this.messageForm.getRawValue()) })
  }
}
