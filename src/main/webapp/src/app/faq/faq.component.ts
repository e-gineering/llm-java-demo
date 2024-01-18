import { Component } from '@angular/core';
import { StreamingLlmChatComponent } from "../streaming-llm-chat/streaming-llm-chat.component";

@Component({
  selector: 'app-faq',
  standalone: true,
    imports: [
        StreamingLlmChatComponent
    ],
  templateUrl: './faq.component.html',
  styleUrl: './faq.component.scss'
})
export class FaqComponent {

  title="Chat GPT With FAQ"
  topic="/topic/faq/llmResponse"
  streamingTopic="/topic/faq/llmStreamingResponse"
  submitDestination="/app/faq/llmStreamingRequest"
  chartDefBasic = `sequenceDiagram
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
}
