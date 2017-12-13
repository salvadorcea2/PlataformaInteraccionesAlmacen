import {Component, Injectable} from '@angular/core';
import { AuthService } from '../auth/auth.service';

@Component({
  selector: 'app-topbar',
  templateUrl: './topbar.component.html',
  providers: []
})
@Injectable()
export class TopbarComponent {
  constructor(public authService: AuthService){ }
  logout() {
    this.authService.logout();
  }
}
