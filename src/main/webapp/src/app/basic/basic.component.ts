import { Component } from '@angular/core';
import { StreamingLlmChatComponent } from "../streaming-llm-chat/streaming-llm-chat.component";

@Component({
  selector: 'app-basic',
  standalone: true,
    imports: [
        StreamingLlmChatComponent
    ],
  templateUrl: './basic.component.html',
  styleUrl: './basic.component.scss'
})
export class BasicComponent {
  title="ChatGPT"
  topic="/topic/basic/llmResponse"
  streamingTopic="/topic/basic/llmStreamingResponse"
  submitDestination="/app/basic/llmStreamingRequest"
  resetDestination="/app/basic/reset"
  flowDiagram = `sequenceDiagram
    actor User
    User ->> Demo UI: Message
    box Grey E-gineering Code
    participant Demo UI
    participant Demo App
    end
    Demo UI ->>+ Demo App: User message
    Demo App ->>+ ChatGPT: Streaming API call<br />[user message]
    ChatGPT ->> Demo App: Streaming response
        loop Stream
        Demo App -) Demo App: Receive Tokens
    end
    ChatGPT -)- Demo App: Token stream
    Demo App -)- Demo UI: Token stream
    Demo UI ->> User: Response`
}
