import {Component, Injectable, OnInit} from '@angular/core';
import { AuthService } from './auth/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  providers: []
})
@Injectable()
export class AppComponent implements OnInit {
  login = false;


  constructor(private router: Router, public authService: AuthService){
    /*let loggedIn = false;
    console.log('LOGIN: ' + loggedIn);

    this.authService.login()
      .subscribe(
        function(response) {
          if(response.status === 200) {
            loggedIn = true;
            console.log(response.json());
            authService.setSesion(response.json());
          }
        },
        function(error) {
          console.log('Error happened' + error);
        },
        function() {
          //let router: Router;
          console.log('LOGIN: ' + loggedIn + ' | ' + authService.isAuth());
          if (!loggedIn) {
            console.log('Not loggedIn!');
            //router.navigate(['/login']);
          }else{
            if(authService.isAdmin()){
              router.navigate(['/usuarios']);
            }else if(authService.isUsuario()){
              router.navigate(['/recepciones']);
            }else{
              router.navigate(['/']);
            }
          }
        }
      );*/
  }

  ngOnInit() { }

}
