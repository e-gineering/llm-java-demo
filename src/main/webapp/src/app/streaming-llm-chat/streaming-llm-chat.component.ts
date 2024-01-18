import { AfterViewInit, Component, ElementRef, Input, OnInit, ViewChild } from '@angular/core';
import { MatButtonModule } from "@angular/material/button";
import { MatCardModule } from "@angular/material/card";
import { MatDividerModule } from "@angular/material/divider";
import { MatExpansionModule } from "@angular/material/expansion";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { NgForOf, NgIf } from "@angular/common";
import { FormControl, FormGroup, ReactiveFormsModule } from "@angular/forms";
import { ChatMessage } from "../models/ChatMessage";
import { RxStompService } from "../services/rx-stomp.service";
import { IMessage } from "@stomp/rx-stomp";
import mermaid from "mermaid";
import { LoaderService } from "../services/loader.service";

@Component({
  selector: 'app-streaming-llm-chat',
  standalone: true,
    imports: [
        MatButtonModule,
        MatCardModule,
        MatDividerModule,
        MatExpansionModule,
        MatFormFieldModule,
        MatInputModule,
        NgForOf,
        ReactiveFormsModule,
        NgIf
    ],
  templateUrl: './streaming-llm-chat.component.html',
  styleUrl: './streaming-llm-chat.component.scss'
})
export class StreamingLlmChatComponent implements OnInit, AfterViewInit {

    @Input() id: string = '';
    @Input() title: string = '';
    @Input() topic: string = '';
    @Input() streamingTopic: string = '';
    @Input() submitDestination: string = '';
    @Input() resetDestination: string = '';
    @Input() preFlowDiagram: string = '';
    @Input() flowDiagram: string = '';

    messageForm = new FormGroup({message: new FormControl()});
    response: { messages: ChatMessage[], documents: string[], files: string[]  } = {messages: [], documents: [], files: []};
    responseStream: { response: string } = {response: ''};

    @ViewChild('message') message: ElementRef | undefined;
    @ViewChild('mermaidDiv') mermaidDiv: ElementRef | undefined;
    @ViewChild('preMermaidDiv') preMermaidDiv: ElementRef | undefined;

    constructor(private rxStompService: RxStompService, protected loaderService: LoaderService) {}

    ngOnInit(): void {
        this.rxStompService.watch(this.topic).subscribe((message: IMessage) => {
            this.messageForm.get('message')?.enable();
            this.response.messages = JSON.parse(message.body).messages;
            this.response.documents = JSON.parse(message.body).documents;
            this.response.files = JSON.parse(message.body).files;
            setTimeout(() => this.message?.nativeElement.focus(), 0);
        });

        this.rxStompService.watch(this.streamingTopic).subscribe((message: IMessage) => {
            this.responseStream.response = this.responseStream.response ? this.responseStream.response + JSON.parse(message.body).response : JSON.parse(message.body).response;
            if (this.responseStream.response) {
                this.loaderService.setLoading(false);
            }
        });
    }

    ngAfterViewInit(): void {
        if (this.preFlowDiagram) {
            const preMermaidDiv: any = this.preMermaidDiv?.nativeElement;
            mermaid.render(`preMermaidDiv${this.id}`, this.preFlowDiagram)
                .then(value => preMermaidDiv.innerHTML = value.svg);
        }

        if (this.flowDiagram) {
            const mermaidDiv: any = this.mermaidDiv?.nativeElement;
            mermaid.render(`mermaidDiv${this.id}`, this.flowDiagram)
                .then(value => mermaidDiv.innerHTML = value.svg);
        }
    }

    submitMessage() {
        // this.response.messages = [];
        // this.response.documents = [];
        // this.response.files = [];
        this.responseStream.response = '';
        this.loaderService.setLoading(true);
        this.rxStompService.publish({ destination: this.submitDestination, body: JSON.stringify(this.messageForm.getRawValue()) })
        this.messageForm.get('message')?.disable();
        this.messageForm.reset({message: null})
    }

    resetMemory() {
        this.response.messages = [];
        this.response.documents = [];
        this.response.files = [];
        this.responseStream.response = '';
        this.rxStompService.publish({ destination: this.resetDestination, body: "true" })
        this.messageForm.reset({message: null})
        this.message?.nativeElement.focus();
    }
}
