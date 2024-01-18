import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StreamingLlmChatComponent } from './streaming-llm-chat.component';

describe('StreamingLlmChatComponent', () => {
  let component: StreamingLlmChatComponent;
  let fixture: ComponentFixture<StreamingLlmChatComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StreamingLlmChatComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(StreamingLlmChatComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
