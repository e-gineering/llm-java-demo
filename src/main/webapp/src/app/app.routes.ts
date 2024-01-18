import { Routes } from '@angular/router';
import { MainComponent } from "./main/main.component";
import { BasicComponent } from "./basic/basic.component";

export const routes: Routes = [
  {path: '', component: MainComponent},
  {path: 'basic', component: BasicComponent}
];
