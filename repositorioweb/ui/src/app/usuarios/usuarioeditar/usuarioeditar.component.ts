import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Ministerio } from '../../models/ministerio.model';
import { Subsecretaria } from '../../models/subsecretaria.model';
import { Institucion } from '../../models/institucion.model';
import { Usuario } from '../../models/usuario.model';
import { BsModalService } from 'ngx-bootstrap/modal';
import { BsModalRef } from 'ngx-bootstrap/modal/modal-options.class';
import { ApiService } from '../../services/api.service';
import { AlertasService } from '../../services/alertas.service';
import { Alerta } from '../../models/alerta.model';
import { NgForm } from '@angular/forms';

@Component({
  selector: 'app-usuarioeditar',
  templateUrl: './usuarioeditar.component.html',
  styleUrls: ['./usuarioeditar.component.css']
})
export class UsuarioeditarComponent implements OnInit {

  public _component_entidad: string = 'usuario';
  public _component_entidad_id: number;

  public _lista_ministerio : number;
  public _lista_ministerios: Ministerio[] = [];
  public _lista_subsecretarias: Subsecretaria[] = [];
  public _lista_instituciones: Institucion[] = [];
  public item: Usuario = new Usuario({});
  public modalRef: BsModalRef;

  constructor(private _apiService: ApiService, private _alertasService: AlertasService, private router: Router, private route: ActivatedRoute, private modalService: BsModalService) { }

  getListas() {
    this._apiService.getLista('ministerio')
      .subscribe(
        (data: any[]) => {
          this._lista_ministerios = [];
          if ( data.length > 0 ) {
            for (const d of data) {
              this._lista_ministerios.push( new Ministerio(d) );
            }
          }
        },
        (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error al cargar lista de ministerios'})),
        () => this._apiService.getLista('subsecretaria')
          .subscribe(
            (data: any[]) => {
              this._lista_subsecretarias = [];
              if ( data.length > 0 ) {
                for (const d of data) {
                  this._lista_subsecretarias.push( new Subsecretaria(d) );
                }
              }
            },
            (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error al cargar lista de subsecretarías'})),
            () => this._apiService.getLista('institucion')
              .subscribe(
                (data: any[]) => {
                  this._lista_instituciones = [];
                  if ( data.length > 0 ) {
                    for (const d of data) {
                      this._lista_instituciones.push( new Institucion(d) );
                    }
                  }
                },
                (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error al cargar lista de instituciones'})),
                () => this.updateItem()
              )
          )
      );
  }

  updateItem() {
    if ( typeof this._component_entidad_id !== 'undefined' && this._component_entidad_id !== null ) {
      this._apiService.load(this._component_entidad, this._component_entidad_id)
        .subscribe(
          (data: any) => {
            this.item = new Usuario(data.pop());
          },
          (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error al cargar el usuario ID: ' + this._component_entidad_id}))
        );
    }
  }

  onSubmit(frm: NgForm){
    if ( !frm.valid ) {
      this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error en el formulario, favor revisa nuevamente'}));
      return;
    }

    this.item.ministerio_id = +this.item.ministerio_id;
    this.item.subsecretaria_id = +this.item.subsecretaria_id;
    this.item.institucion_id = +this.item.institucion_id;

    if ( typeof this._component_entidad_id === 'undefined' || this._component_entidad_id === null ) {
      this._apiService.crear(this._component_entidad, this.item)
        .subscribe(
          (data: any) => {
            this._component_entidad_id = Number(data[0].id);
          },
          (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: error.text()})),
          () => this.router.navigate(['/usuarios', Number(this._component_entidad_id), 'ver'])
        );
    }else {
      this._apiService.actualizar(this._component_entidad, this.item)
        .subscribe(
          (data: any) => {
            this._component_entidad_id = Number(data[0].id);
          },
          (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: error.text()})),
          () => this.router.navigate(['/usuarios', Number(this._component_entidad_id), 'ver'])
        );
    }
  }

  ngOnInit() {
    this._component_entidad_id = this.route.snapshot.params['id'];
    this.getListas();
  }
}
