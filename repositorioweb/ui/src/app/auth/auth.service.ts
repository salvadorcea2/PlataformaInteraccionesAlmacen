import { Inject, Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { ApiService } from '../services/api.service';
import { Sesion } from '../models/sesion.model';
import {environment} from '../../environments/environment';
import { DOCUMENT } from '@angular/common';
import { Observable } from 'rxjs/Observable';

// Observable class extensions
import 'rxjs/add/observable/of';
import 'rxjs/add/observable/throw';

// Observable operators
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';

@Injectable()
export class AuthService {

  private sesion: Sesion;

  constructor(private http: Http, private apiService: ApiService, @Inject(DOCUMENT) private document: any) { }

  isAuth(): boolean {
    return this.sesion != null;
  }

  isAdmin(): boolean {
    return this.sesion != null && this.sesion.rol === 'admin';
  }

  isUsuario(): boolean {
    return this.sesion != null && this.sesion.rol === 'institucion';
  }

  getSesion(): Observable<Sesion> {
    if ( this.isAuth() ) {
      return Observable.of(this.sesion);
    }
    this.apiService.dialogo('abrir');
    return this.http.get(environment.api.login)
      .map(
        (response: Response) => {
          this.apiService.dialogo('cerrar');
          this.sesion = new Sesion(response.json().data[0]);
          return this.sesion;
        }
      )
      .catch(
        (error: Response) => {
          this.apiService.dialogo('cerrar');
          return Observable.throw('Error al obtener sesion');
        }
      );
  }

  setSesion(json: any) {
    const d = json.data.pop();
    this.sesion = new Sesion({id: d.id, usuario: d.usuario, nombres: d.nombres, apellidos: d.apellidos, email: d.email, telefono: d.telefono, rut: d.rut, rol: d.rol, tipo: d.tipo, cargo: d.cargo, ministerio_id: d.ministerio_id, subsecretaria_id: d.subsecretaria_id, institucion_id: d.institucion_id});
    // console.log('id=='+this.sesion.id);
    // this.sesion = new Sesion(d.id, d.usuario, d.nombres, d.apellidos, d.email, d.telefono, d.rut, d.rol, d.tipo, d.cargo, d.habilitado);
  }

  login() {
    console.log('Loggin...');
    return this.http.get(environment.api.login);
    // return this.http.get('/api/usuario/sesion');
    // return this.http.get('https://54.187.41.133:8080/api/usuario/sesion');
    // return this.http.get('http://localhost/segpres/json.php?servicio=login');
    // return this.http.get('http://192.168.8.100/segpres/json.php');
  }

  forceLogin() {
    return this.http.get('https://54.187.41.133:8080/callback/1');
  }

  logout() {
    setTimeout(function(){ this.document.location.href = '/logout'; }, 3000);
    this.document.location.href = environment.api.logout;
    // this.document.location.href = '/logout';
    /*this.http.get(environment.api.logout)
      .subscribe(
        (data: any) => {
          console.log(data);
          // this.document.location.href = '/logout';
          // window.location.href = '/logout';
        },
        (error: any) => console.log('error: ' + error),
        () => {
          this.document.location.href = '/logout';
          // window.location.href = '/logout';
        }
      );*/
  }

  logout_orig() {
    this.http.delete(environment.api.logout)
      .subscribe(
        (data: any) => {
          console.log(data);
          // this.document.location.href = '/logout';
          // window.location.href = '/logout';
        },
        (error: any) => console.log('error: ' + error),
        () => {
          console.log('complete');
          // this.document.location.href = '/logout';
          // window.location.href = '/logout';
        }
      );
  }

  logout_test2() {
    this.http.delete(environment.api.logout)
      .subscribe(
        (data: any) => this.document.location.href = '/logout',
        (error: any) => console.log('error: ' + error),
        () => this.document.location.href = '/logout'
      );
  }

}
