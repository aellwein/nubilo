import { Injectable } from '@angular/core';
import {environment} from '../environments/environment';

@Injectable()
export class LoginService {
  private environment;

  constructor() {
    this.environment = environment;
  }


}
