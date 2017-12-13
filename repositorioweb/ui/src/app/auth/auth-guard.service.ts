import { ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot } from '@angular/router';
import { AuthService } from './auth.service';
import { Injectable } from '@angular/core';
import { RecepcionesComponent } from '../recepciones/recepciones.component';
import { BitacoraComponent } from '../recepciones/bitacora/bitacora.component';
import { DetallerecepcionComponent } from '../recepciones/detalle/detallerecepcion.component';
import { FactoresrecepcionComponent } from '../recepciones/factores/factoresrecepcion.component';
import { UploadComponent } from '../recepciones/upload/upload.component';
import { Sesion } from '../models/sesion.model';
import { Observable } from 'rxjs/Observable';

// Observable class extensions
import 'rxjs/add/observable/of';

// Observable operators
import 'rxjs/add/operator/map';

@Injectable()
export class AuthGuard implements CanActivate {
  constructor(private authService: AuthService) {}
  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
    return this.authService.getSesion()
      .map(
        (sesion: Sesion) => {
          switch (route.component) {
            case RecepcionesComponent :
            case BitacoraComponent :
            case DetallerecepcionComponent :
            case FactoresrecepcionComponent :
            case UploadComponent :
              return this.authService.isUsuario();
            default:
              return this.authService.isAdmin();
          }
        }
      );
    /*switch (route.component) {
      case RecepcionesComponent :
      case BitacoraComponent :
      case DetallerecepcionComponent :
      case UploadComponent :
        return this.authService.isUsuario();
      case UsuariosComponent :
      case UsuarioeditarComponent :
      case UsuarioverComponent :
      case TramitesComponent :
      case InstitucionesComponent :
      case InstitucionverComponent :
      case InstitucioneditarComponent :
        return this.authService.isAdmin();
      default:
        return this.authService.isAuth();
    }*/
  }
}
