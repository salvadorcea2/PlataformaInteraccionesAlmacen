import { Component, Injectable, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../auth/auth.service';
import { Sesion } from '../models/sesion.model';


@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  providers: [AuthService]
})
@Injectable()
export class HomeComponent implements OnInit {

  constructor(private router: Router, private authService: AuthService) { }

  ngOnInit() {
    console.log('HomeComponent');
    this.authService.getSesion()
      .subscribe(
        (sesion: Sesion) => {
          if(this.authService.isAdmin()){
            console.log('isAdmin');
            this.router.navigate(['/usuarios']);
          }else if(this.authService.isUsuario()){
            console.log('isUsuario');
            this.router.navigate(['/recepciones']);
          }else{
            console.log('esNada!');
            this.router.navigate(['/']);
          }
        }
      );
  }

}
