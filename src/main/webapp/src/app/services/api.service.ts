import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { FormGroup } from "@angular/forms";

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  constructor(private httpClient: HttpClient) {}

  postMessageBasic(messageForm: FormGroup) {
    return this.httpClient.post<{response: string}>("/api/generateBasic", messageForm.value, {headers: {"Content-Type": "application/json"}});
  }

  postMessageFaq(messageForm: FormGroup) {
    return this.httpClient.post<{response: string}>("/api/generateFaq", messageForm.value, {headers: {"Content-Type": "application/json"}});
  }

  postMessageDocs(messageForm: FormGroup) {
    return this.httpClient.post<{response: string}>("/api/generateDocs", messageForm.value, {headers: {"Content-Type": "application/json"}});
  }

  postMessageData(messageForm: FormGroup) {
    return this.httpClient.post<{response: string}>("/api/generateData", messageForm.value, {headers: {"Content-Type": "application/json"}});
  }}
