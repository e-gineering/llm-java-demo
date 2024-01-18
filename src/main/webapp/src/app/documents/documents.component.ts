import { Component } from '@angular/core';
import { StreamingLlmChatComponent } from "../streaming-llm-chat/streaming-llm-chat.component";

@Component({
  selector: 'app-documents',
  standalone: true,
    imports: [
        StreamingLlmChatComponent
    ],
  templateUrl: './documents.component.html',
  styleUrl: './documents.component.scss'
})
export class DocumentsComponent {

  title="Chat GPT With Documents"
  topic="/topic/documents/llmResponse"
  streamingTopic="/topic/documents/llmStreamingResponse"
  submitDestination="/app/documents/llmStreamingRequest"
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
