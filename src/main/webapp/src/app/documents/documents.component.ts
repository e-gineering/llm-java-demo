import { Component } from '@angular/core';
import { StreamingLlmChatComponent } from "../streaming-llm-chat/streaming-llm-chat.component";
import { MatDividerModule } from "@angular/material/divider";

@Component({
  selector: 'app-documents',
  standalone: true,
  imports: [
    StreamingLlmChatComponent,
    MatDividerModule
  ],
  templateUrl: './documents.component.html',
  styleUrl: './documents.component.scss'
})
export class DocumentsComponent {

  title= "Chat GPT With Documents"
  topic= "/topic/documents/llmResponse"
  streamingTopic= "/topic/documents/llmStreamingResponse"
  submitDestination= "/app/documents/llmStreamingRequest"
  resetDestination= "/app/documents/reset"
  preFlowDiagram = `flowchart LR
    head((Document Preparation))
    docs(Documents)
    in(Ingestor)
    emb{Transformation to Embeddings<br />#lpar;Chunks of Text#rpar;}
    data[(Vector Store<br />#lpar;Chroma#rpar;)]
    head~~~docs-->in-->emb-->data`
  flowDiagram = `sequenceDiagram
    actor User
    User ->> Demo UI: User Message
    box Grey E-gineering Code
    participant Demo UI
    participant Demo App
    participant Demo Vector Store as Demo Vector Store<br />#lpar;Chroma#rpar;
    end
    Demo UI ->>+ Demo App: User message
    rect rgb(123,31,162)
    Note over Demo Vector Store: New component
    Demo App ->>+ Demo Vector Store: Query with user message
    Demo Vector Store ->>- Demo App: Relevant text embeddings (information)
    Demo App ->> Demo App: Build system message with relevant information
    Demo App ->>+ Chat GPT: Streaming API call<br />[system message*]<br />[user message]
    end
    Chat GPT ->> Demo App: Streaming response
        loop Stream
        Demo App -) Demo App: Receive Tokens
    end
    Chat GPT -)- Demo App: Token stream
    Demo App -)- Demo UI: Token stream
    Demo UI ->> User: Response`
}
