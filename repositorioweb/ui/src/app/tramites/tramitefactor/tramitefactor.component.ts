import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Event, Router } from '@angular/router';
import { Tramite, TramiteFactor } from '../../models/tramite.model';
import { MantenedorItem } from '../../models/mantenedor.model';
import { NgForm } from '@angular/forms';
import {ApiService} from '../../services/api.service';
import {AlertasService} from '../../services/alertas.service';
import {Alerta} from '../../models/alerta.model';

@Component({
  selector: 'app-tramitefactor',
  templateUrl: './tramitefactor.component.html',
  styleUrls: ['./tramitefactor.component.css']
})
export class TramitefactorComponent implements OnInit {

  public _component_entidad: string = 'tipotramite';
  public _component_entidad_id: number;
  public _component_factor_id: number;

  public _lista_tipo_interacciones: MantenedorItem[] = [];
  public _lista_factores: TramiteFactor[] = [];
  public _parent: Tramite = new Tramite({});
  public _item: TramiteFactor = new TramiteFactor({id: 0, tipo_interaccion_id: ''});

  constructor(private _apiService: ApiService, private _alertasService: AlertasService, private router: Router, private route: ActivatedRoute) { }

  getListas() {
    this._apiService.getLista('mantenedor/tipo_interaccion')
      .subscribe(
        (data: any[]) => {
          this._lista_tipo_interacciones = [];
          if ( data.length > 0 ) {
            for (const d of data) {
              this._lista_tipo_interacciones.push( new MantenedorItem(d) );
            }
          }
        },
        (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error al cargar lista de tipo de interacciones'})),
        () => this.updateItem()
      );
  }

  updateItem() {
    if ( typeof this._component_entidad_id !== 'undefined' && this._component_entidad_id !== null && this._component_entidad_id !== 0 ) {
      this._apiService.load(this._component_entidad, this._component_entidad_id)
        .subscribe(
          (data: any) => {
            this._parent = new Tramite(data.pop());
          },
          (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error al cargar el trámite ID: ' + this._component_entidad_id})),
          () => this._apiService.getLista(this._component_entidad + '/factor/' + this._component_entidad_id, {orden: 'tipo_tramite_id'})
            .subscribe(
              (data: any[]) => {
                this._lista_factores = [];
                if ( data.length > 0 ) {
                  for (const d of data) {
                    this._lista_factores.push( new TramiteFactor(d) );
                    if( Number(d.id) === Number(this._component_factor_id) ) {
                      this._item = new TramiteFactor(d);
                    }
                  }
                }
              },
              (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error al buscar los permisos jerárquicos del receptor'}))
            )
        );
    }
  }

  onSubmit(frm: NgForm){
    if ( !frm.valid ) {
      this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error en el formulario, favor revisa nuevamente'}));
      return;
    }

    this._item.tipo_tramite_id = +this._component_entidad_id;
    this._item.tipo_interaccion_id = +this._item.tipo_interaccion_id;
    this._item.factor = +this._item.factor;

    if ( typeof this._component_factor_id === 'undefined' || this._component_factor_id === null || this._component_factor_id === 0 ) {
      this._apiService.crear(this._component_entidad + '/factor', this._item)
        .subscribe(
          (data: any) => {
            this._component_factor_id = Number(data[0].id);
          },
          (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: error.text()})),
          () => this.router.navigate(['/tramites', Number(this._component_entidad_id), 'ver'])
        );
    }else {
      this._apiService.actualizar(this._component_entidad + '/factor', this._item)
        .subscribe(
          (data: any) => {
            this._component_factor_id = Number(data[0].id);
          },
          (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: error.text()})),
          () => this.router.navigate(['/tramites', Number(this._component_entidad_id), 'ver'])
        );
    }
  }

  ngOnInit() {
    this._component_entidad_id = +this.route.snapshot.params['id'];
    this._component_factor_id = +this.route.snapshot.params['factor'];
    this.getListas();
  }

}
