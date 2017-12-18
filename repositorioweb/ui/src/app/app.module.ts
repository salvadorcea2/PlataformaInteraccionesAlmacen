import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { NgbModule, NgbDatepickerModule } from '@ng-bootstrap/ng-bootstrap';
import { ButtonsModule } from 'ngx-bootstrap/buttons';
import { BsDatepickerModule } from 'ngx-bootstrap/datepicker';
import { defineLocale } from 'ngx-bootstrap/bs-moment';
import { es } from 'ngx-bootstrap/locale';
import { ModalModule } from 'ngx-bootstrap/modal';
import { AlertModule } from 'ngx-bootstrap/alert';

import { AppComponent } from './app.component';
import { ApiService } from './services/api.service';
import { AlertasService } from './services/alertas.service';
import { TopbarComponent } from './topbar/topbar.component';
import { MenuComponent } from './menu/menu.component';
import { RecepcionesComponent } from './recepciones/recepciones.component';
import { UploadComponent } from './recepciones/upload/upload.component';
import { BitacoraComponent } from './recepciones/bitacora/bitacora.component';
import { DetallerecepcionComponent } from './recepciones/detalle/detallerecepcion.component';
import { FactoresrecepcionComponent } from './recepciones/factores/factoresrecepcion.component';
import { Routes, RouterModule } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { LoginComponent } from './auth/login/login.component';
import { AuthService } from './auth/auth.service';
import { AuthGuard } from './auth/auth-guard.service';
import { PaginationService } from './services/pagination.service';
import { UsuariosComponent } from './usuarios/usuarios.component';
import { TramitesComponent } from './tramites/tramites.component';
import { TramiteeditarComponent } from './tramites/tramiteeditar/tramiteeditar.component';
import { TramiteverComponent } from './tramites/tramitever/tramitever.component';
import { InstitucionesComponent } from './instituciones/instituciones.component';
import { UsuarioeditarComponent } from './usuarios/usuarioeditar/usuarioeditar.component';
import { UsuarioverComponent } from './usuarios/usuariover/usuariover.component';
import { InstitucionverComponent } from './instituciones/institucionver/institucionver.component';
import { InstitucioneditarComponent } from './instituciones/institucioneditar/institucioneditar.component';
import { MantenedoresComponent } from './mantenedores/mantenedores.component';
import { MantenedoreditarComponent } from './mantenedores/mantenedoreditar/mantenedoreditar.component';
import { MantenedorverComponent } from './mantenedores/mantenedorver/mantenedorver.component';
import { ReceptoresComponent } from './receptores/receptores.component';
import { ReceptoreditarComponent } from './receptores/receptoreditar/receptoreditar.component';
import { ReceptorverComponent } from './receptores/receptorver/receptorver.component';
import { ReceptormascaraComponent } from './receptores/receptormascara/receptormascara.component';
import { MinisteriosComponent } from './ministerios/ministerios.component';
import { MinisterioeditarComponent } from './ministerios/ministerioeditar/ministerioeditar.component';
import { MinisterioverComponent } from './ministerios/ministeriover/ministeriover.component';
import { SubsecretariasComponent } from './subsecretarias/subsecretarias.component';
import { SubsecretariaeditarComponent } from './subsecretarias/subsecretariaeditar/subsecretariaeditar.component';
import { SubsecretariaverComponent } from './subsecretarias/subsecretariaver/subsecretariaver.component';
import { SeleccionableComponent } from './html/seleccionable/seleccionable.component';
import { ForeignitemPipe } from './pipes/foreignitem.pipe';
import { TramitefactorComponent } from './tramites/tramitefactor/tramitefactor.component';
import { AlertsComponent } from './alerts/alerts.component';
import { TramitehistoricoComponent } from './tramites/tramitehistorico/tramitehistorico.component';
import { PlantillasComponent } from './plantillas/plantillas.component';
import { PlantillaeditarComponent } from './plantillas/plantillaeditar/plantillaeditar.component';
import { PlantillaverComponent } from './plantillas/plantillaver/plantillaver.component';
import { ServiciosComponent } from './servicios/servicios.component';
import { ServicioverComponent } from './servicios/serviciover/serviciover.component';
import { ServicioeditarComponent } from './servicios/servicioeditar/servicioeditar.component';
import { DialogoComponent } from './html/dialogo/dialogo.component';
import { FiltroPipe } from './pipes/filtro.pipe';
import { PaginadorComponent } from './html/paginador/paginador.component';
import { AlertasComponent } from './html/alertas/alertas.component';
import { FromlistaPipe } from './pipes/fromlista.pipe';
import { NoWhitespaceDirective } from './directives/no-whitespace.directive';
import { FechaPipe } from './pipes/fecha.pipe';

const appRoutes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'recepciones', component: RecepcionesComponent, canActivate: [AuthGuard] },
  { path: 'recepciones/upload', component: UploadComponent, canActivate: [AuthGuard] },
  { path: 'recepciones/:id/bitacora', component: BitacoraComponent, canActivate: [AuthGuard] },
  { path: 'recepciones/:id/detalle', component: DetallerecepcionComponent, canActivate: [AuthGuard] },
  { path: 'recepciones/:id/factores', component: FactoresrecepcionComponent, canActivate: [AuthGuard] },
  { path: 'usuarios', component: UsuariosComponent, canActivate: [AuthGuard] },
  { path: 'usuarios/nuevo', component: UsuarioeditarComponent, canActivate: [AuthGuard] },
  { path: 'usuarios/:id/editar', component: UsuarioeditarComponent, canActivate: [AuthGuard] },
  { path: 'usuarios/:id/ver', component: UsuarioverComponent, canActivate: [AuthGuard] },
  { path: 'ministerios', component: MinisteriosComponent, canActivate: [AuthGuard] },
  { path: 'ministerios/nuevo', component: MinisterioeditarComponent, canActivate: [AuthGuard] },
  { path: 'ministerios/:id/editar', component: MinisterioeditarComponent, canActivate: [AuthGuard] },
  { path: 'ministerios/:id/ver', component: MinisterioverComponent, canActivate: [AuthGuard] },
  { path: 'subsecretarias', component: SubsecretariasComponent, canActivate: [AuthGuard] },
  { path: 'subsecretarias/nuevo', component: SubsecretariaeditarComponent, canActivate: [AuthGuard] },
  { path: 'subsecretarias/:id/editar', component: SubsecretariaeditarComponent, canActivate: [AuthGuard] },
  { path: 'subsecretarias/:id/ver', component: SubsecretariaverComponent, canActivate: [AuthGuard] },
  { path: 'tramites', component: TramitesComponent, canActivate: [AuthGuard] },
  { path: 'tramites/nuevo', component: TramiteeditarComponent, canActivate: [AuthGuard] },
  { path: 'tramites/:id/editar', component: TramiteeditarComponent, canActivate: [AuthGuard] },
  { path: 'tramites/:id/ver', component: TramiteverComponent, canActivate: [AuthGuard] },
  { path: 'tramites/:id/factor/:factor', component: TramitefactorComponent, canActivate: [AuthGuard] },
  { path: 'tramites/:id/historicos', component: TramitehistoricoComponent, canActivate: [AuthGuard] },
  { path: 'instituciones', component: InstitucionesComponent, canActivate: [AuthGuard] },
  { path: 'instituciones/nueva', component: InstitucioneditarComponent, canActivate: [AuthGuard] },
  { path: 'instituciones/:id/editar', component: InstitucioneditarComponent, canActivate: [AuthGuard] },
  { path: 'instituciones/:id/ver', component: InstitucionverComponent, canActivate: [AuthGuard] },
  { path: 'receptores', component: ReceptoresComponent, canActivate: [AuthGuard] },
  { path: 'receptores/nuevo', component: ReceptoreditarComponent, canActivate: [AuthGuard] },
  { path: 'receptores/:id/editar', component: ReceptoreditarComponent, canActivate: [AuthGuard] },
  { path: 'receptores/:id/ver', component: ReceptorverComponent, canActivate: [AuthGuard] },
  { path: 'receptores/:id/mascara/:mascara', component: ReceptormascaraComponent, canActivate: [AuthGuard] },
  { path: 'plantillas', component: PlantillasComponent, canActivate: [AuthGuard] },
  { path: 'plantillas/nuevo', component: PlantillaeditarComponent, canActivate: [AuthGuard] },
  { path: 'plantillas/:id/editar', component: PlantillaeditarComponent, canActivate: [AuthGuard] },
  { path: 'plantillas/:id/ver', component: PlantillaverComponent, canActivate: [AuthGuard] },
  { path: 'servicios', component: ServiciosComponent, canActivate: [AuthGuard] },
  { path: 'servicios/nuevo', component: ServicioeditarComponent, canActivate: [AuthGuard] },
  { path: 'servicios/:id/editar', component: ServicioeditarComponent, canActivate: [AuthGuard] },
  { path: 'servicios/:id/ver', component: ServicioverComponent, canActivate: [AuthGuard] },
  { path: 'mantenedores', component: MantenedoresComponent, canActivate: [AuthGuard] },
  { path: 'mantenedores/nuevo', component: MantenedoreditarComponent, canActivate: [AuthGuard] },
  { path: 'mantenedores/:tabla/:id/editar', component: MantenedoreditarComponent, canActivate: [AuthGuard] },
  { path: 'mantenedores/:tabla/:id/ver', component: MantenedorverComponent, canActivate: [AuthGuard] },
];

@NgModule({
  declarations: [
    AppComponent,
    TopbarComponent,
    MenuComponent,
    RecepcionesComponent,
    UploadComponent,
    BitacoraComponent,
    DetallerecepcionComponent,
    FactoresrecepcionComponent,
    HomeComponent,
    LoginComponent,
    UsuariosComponent,
    TramitesComponent,
    TramiteeditarComponent,
    TramiteverComponent,
    InstitucionesComponent,
    UsuarioeditarComponent,
    UsuarioverComponent,
    InstitucionverComponent,
    InstitucioneditarComponent,
    MantenedoresComponent,
    MantenedoreditarComponent,
    MantenedorverComponent,
    ReceptoresComponent,
    ReceptoreditarComponent,
    ReceptorverComponent,
    ReceptormascaraComponent,
    SeleccionableComponent,
    ForeignitemPipe,
    MinisteriosComponent,
    MinisterioeditarComponent,
    MinisterioverComponent,
    SubsecretariasComponent,
    SubsecretariaeditarComponent,
    SubsecretariaverComponent,
    TramitefactorComponent,
    AlertsComponent,
    TramitehistoricoComponent,
    PlantillasComponent,
    PlantillaeditarComponent,
    PlantillaverComponent,
    DialogoComponent,
    FiltroPipe,
    PaginadorComponent,
    AlertasComponent,
    FromlistaPipe,
    NoWhitespaceDirective,
    FechaPipe,
    ServiciosComponent,
    ServicioverComponent,
    ServicioeditarComponent
  ],
  entryComponents : [
    DialogoComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule,
    RouterModule.forRoot(appRoutes, { useHash: true }),
    NgbModule.forRoot(),
    NgbDatepickerModule.forRoot(),
    ButtonsModule.forRoot(),
    ModalModule.forRoot(),
    AlertModule.forRoot(),
    BsDatepickerModule.forRoot()
  ],
  providers: [ApiService, AlertasService, FechaPipe, FiltroPipe, FromlistaPipe, AuthService, AuthGuard, PaginationService, { provide: 'Window',  useValue: window }],
  bootstrap: [AppComponent]
})
export class AppModule {
  constructor() {
    defineLocale('es', es);
  }
}
