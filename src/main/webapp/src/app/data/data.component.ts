import { Component } from '@angular/core';
import { StreamingLlmChatComponent } from "../streaming-llm-chat/streaming-llm-chat.component";

@Component({
  selector: 'app-data',
  standalone: true,
    imports: [
        StreamingLlmChatComponent
    ],
  templateUrl: './data.component.html',
  styleUrl: './data.component.scss'
})
export class DataComponent {

  title="Chat GPT With Data"
  topic="/topic/data/llmResponse"
  streamingTopic="/topic/data/llmStreamingResponse"
  submitDestination="/app/data/llmStreamingRequest"
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
