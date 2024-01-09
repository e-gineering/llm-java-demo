import { Injectable } from '@angular/core';
import { RxStomp } from "@stomp/rx-stomp";
import { rxStompConfig } from "../rx-stomp.config";

@Injectable({
  providedIn: 'root'
})
export class RxStompService extends RxStomp {

  constructor() {
    super();
    this.configure(rxStompConfig);
    this.activate();
  }
}

export function rxStompServiceFactory() {
  const rxStomp = new RxStompService();
  rxStomp.configure(rxStompConfig);
  rxStomp.activate();
  return rxStomp;
}
