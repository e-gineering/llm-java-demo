import { Component } from '@angular/core';
import { StreamingLlmChatComponent } from "../streaming-llm-chat/streaming-llm-chat.component";
import { MatDividerModule } from "@angular/material/divider";

@Component({
  selector: 'app-faq',
  standalone: true,
  imports: [
    StreamingLlmChatComponent,
    MatDividerModule
  ],
  templateUrl: './faq.component.html',
  styleUrl: './faq.component.scss'
})
export class FaqComponent {

  title="ChatGPT With FAQ"
  topic="/topic/faq/llmResponse"
  streamingTopic="/topic/faq/llmStreamingResponse"
  submitDestination="/app/faq/llmStreamingRequest"
  resetDestination="/app/faq/reset"
  flowDiagram = `sequenceDiagram
    actor User
    User ->> Demo UI: Message
    box Grey E-gineering Code
    participant Demo UI
    participant Demo App
    end
    Demo UI ->>+ Demo App: User message
    rect rgb(123,31,162)
    Demo App ->> Demo App: Build system message* with FAQs
    end
    Demo App ->>+ ChatGPT: Streaming API call<br />[system message*]<br />[user message]
    ChatGPT ->> Demo App: Streaming response
        loop Stream
        Demo App -) Demo App: Receive Tokens
    end
    ChatGPT -)- Demo App: Token stream
    Demo App -)- Demo UI: Token stream
    Demo UI ->> User: Response`
}
