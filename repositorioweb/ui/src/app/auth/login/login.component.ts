import { Component, Injectable, OnInit } from '@angular/core';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  providers: []
})
@Injectable()
export class LoginComponent implements OnInit {

  constructor(private authService: AuthService, private router: Router) { }

  ngOnInit() {
    console.log('LoginComponent');
    if ( this.router.url === '/logout' ) {
      this.authService.logout();
      this.router.navigate(['/']);
    }
  }

}
