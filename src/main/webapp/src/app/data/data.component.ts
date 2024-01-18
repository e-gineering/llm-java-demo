import { Component } from '@angular/core';
import { StreamingLlmChatComponent } from "../streaming-llm-chat/streaming-llm-chat.component";
import { MatDividerModule } from "@angular/material/divider";

@Component({
  selector: 'app-data',
  standalone: true,
    imports: [
        StreamingLlmChatComponent,
        MatDividerModule
    ],
  templateUrl: './data.component.html',
  styleUrl: './data.component.scss'
})
export class DataComponent {

  title="Chat GPT With Data"
  topic="/topic/data/llmResponse"
  streamingTopic="/topic/data/llmStreamingResponse"
  submitDestination="/app/data/llmStreamingRequest"
  resetDestination="/app/data/reset"
  preFlowDiagram = `flowchart LR
    head((Data Preparation))
    in(Data)
    cl(Clean and Prep)
    data[(Database<br />#lpar;Postgres#rpar;)]
    head~~~in-->cl-->data`
  flowDiagram = `sequenceDiagram
    actor User
    User ->> Demo UI: User Message
    box Grey E-gineering Code
    participant Demo UI
    participant Demo App
    participant Demo Database
    end
    Demo UI ->>+ Demo App: User message
    rect rgb(123,31,162)
    Note over Demo Database: New component
    Demo App ->>+ ChatGPT: API call<br />[SQL generating system message*]
    ChatGPT ->>- Demo App: Generated query (SELECT * FROM...)
    Demo App ->>+ Demo Database: Query with generated query
    Demo Database ->>- Demo App: Query results
    Demo App ->> Demo App: Build system message with query results
    Demo App ->>+ ChatGPT: Streaming API call<br />[system message**]<br />[user message]
    end
    ChatGPT ->> Demo App: Streaming response
        loop Stream
        Demo App -) Demo App: Receive Tokens
    end
    ChatGPT -)- Demo App: Token stream
    Demo App -)- Demo UI: Token stream
    Demo UI ->> User: Response`
}
