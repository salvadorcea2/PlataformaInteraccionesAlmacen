import {Component, OnInit, TemplateRef} from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Ministerio } from '../../models/ministerio.model';
import { Subsecretaria } from '../../models/subsecretaria.model';
import { Institucion } from '../../models/institucion.model';
import {Tramite, TramiteFactor} from '../../models/tramite.model';
import { BsModalService } from 'ngx-bootstrap/modal';
import { BsModalRef } from 'ngx-bootstrap/modal/modal-options.class';
import { ApiService } from '../../services/api.service';
import { AlertasService } from '../../services/alertas.service';
import { Alerta } from '../../models/alerta.model';
import { NgForm } from '@angular/forms';
import { MantenedorItem } from '../../models/mantenedor.model';
import { FromlistaPipe } from '../../pipes/fromlista.pipe';

@Component({
  selector: 'app-tramiteeditar',
  templateUrl: './tramiteeditar.component.html',
  styleUrls: ['./tramiteeditar.component.css']
})
export class TramiteeditarComponent implements OnInit {

  public _component_entidad: string = 'tipotramite';
  public _component_entidad_id: number;

  public _lista_ministerio : number;
  public _lista_subsecretaria : number;
  public _lista_ministerios: Ministerio[] = [];
  public _lista_subsecretarias: Subsecretaria[] = [];
  public _lista_instituciones: Institucion[] = [];
  public _lista_periodicidades: MantenedorItem[] = [];
  public _lista_categorias: MantenedorItem[] = [];
  public _lista_nivelesdigitalizacion: MantenedorItem[] = [];
  public _lista_tipo_interacciones: MantenedorItem[] = [];
  public item: Tramite = new Tramite({});
  public item_factores: TramiteFactor[] = [];
  public item_factor_eliminar: TramiteFactor;
  public modalRef: BsModalRef;

  constructor(private _apiService: ApiService, private _alertasService: AlertasService, private router: Router, private route: ActivatedRoute, private fromlistaPipe: FromlistaPipe, private modalService: BsModalService) { }

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
                (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error al cargar lista de periodicidades'})),
                () => this._apiService.getLista('mantenedor/periodicidad')
                  .subscribe(
                    (data: any[]) => {
                      this._lista_periodicidades = [];
                      if ( data.length > 0 ) {
                        for (const d of data) {
                          this._lista_periodicidades.push( new MantenedorItem(d) );
                        }
                      }
                    },
                    (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error al cargar lista de periodicidades'})),
                    () => this._apiService.getLista('mantenedor/categoria')
                      .subscribe(
                        (data: any[]) => {
                          this._lista_categorias = [];
                          if ( data.length > 0 ) {
                            for (const d of data) {
                              this._lista_categorias.push( new MantenedorItem(d) );
                            }
                          }
                        },
                        (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error al cargar lista de categorías'})),
                        () => this._apiService.getLista('mantenedor/nivel_digitalizacion')
                          .subscribe(
                            (data: any[]) => {
                              this._lista_nivelesdigitalizacion = [];
                              if ( data.length > 0 ) {
                                for (const d of data) {
                                  this._lista_nivelesdigitalizacion.push( new MantenedorItem(d) );
                                }
                              }
                            },
                            (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error al cargar lista de niveles de digitalización'})),
                            () => this._apiService.getLista('mantenedor/tipo_interaccion')
                              .subscribe(
                                (data: any[]) => {
                                  this._lista_tipo_interacciones = [];
                                  if ( data.length > 0 ) {
                                    for (const d of data) {
                                      this._lista_tipo_interacciones.push( new MantenedorItem(d) );
                                    }
                                  }
                                },
                                (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error al cargar lista de interacciones'})),
                                () => this.updateItem()
                              )
                          )
                      )
                  )
              )
          )
      );
  }

  updateItem() {
    if ( typeof this._component_entidad_id !== 'undefined' && this._component_entidad_id !== null && this._component_entidad_id !== 0 ) {
      this._apiService.load(this._component_entidad, this._component_entidad_id)
        .subscribe(
          (data: any) => {
            this.item = new Tramite(data.pop());
          },
          (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error al cargar el trámite ID: ' + this._component_entidad_id})),
          () => this._apiService.getLista(this._component_entidad + '/factor/' + this._component_entidad_id, {orden: 'tipo_tramite_id'})
            .subscribe(
              (data: any[]) => {
                this.item_factores = [];
                if ( data.length > 0 ) {
                  for (const d of data) {
                    this.item_factores.push( new TramiteFactor(d) );
                  }
                }
              },
              (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error al buscar los factores del trámite'})),
              () => this.loadItem()
            )
        );
    }
  }

  loadItem() {
    let institucion: Institucion = this.fromlistaPipe.transform(this._lista_instituciones, 'id', this.item.institucion_id)[0];
    let subsecretaria: Subsecretaria = this.fromlistaPipe.transform(this._lista_subsecretarias, 'id', institucion.subsecretaria_id)[0];
    this._lista_subsecretaria = subsecretaria.id;
    this._lista_ministerio = subsecretaria.ministerio_id;
  }

  onSubmit(frm: NgForm){
    if ( !frm.valid ) {
      this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error en el formulario, favor revisa nuevamente'}));
      return;
    }

    this.item.institucion_id = +this.item.institucion_id;
    this.item.periodicidad_id = +this.item.periodicidad_id;
    this.item.categoria_id = +this.item.categoria_id;
    this.item.nivel_digitalizacion_id = +this.item.nivel_digitalizacion_id;
    this.item.costo = typeof(this.item.costo) === 'undefined' ? 0 : +this.item.costo;

    if ( typeof this._component_entidad_id === 'undefined' || this._component_entidad_id === null ) {
      this._apiService.crear(this._component_entidad, this.item)
        .subscribe(
          (data: any) => {
            this._component_entidad_id = Number(data[0].id);
          },
          (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: error.text()})),
          () => this.router.navigate(['/tramites', Number(this._component_entidad_id), 'ver'])
        );
    }else {
      this._apiService.actualizar(this._component_entidad, this.item)
        .subscribe(
          (data: any) => {
            this._component_entidad_id = Number(data[0].id);
          },
          (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: error.text()})),
          () => this.router.navigate(['/tramites', Number(this._component_entidad_id), 'ver'])
        );
    }
  }

  onAgregarFactor () {
    this.router.navigate(['/tramites', Number(this._component_entidad_id), 'factor', 0], );
  }

  onEditarFactor (idFactor: number) {
    this.router.navigate(['/tramites', Number(this._component_entidad_id), 'factor', idFactor], );
  }

  onEliminarFactor (template: TemplateRef<any>, factor: TramiteFactor) {
    this.item_factor_eliminar = factor;
    this.modalRef = this.modalService.show(template);
  }

  _onEliminarFactor () {
    this.modalRef.hide();
    this._apiService.borrar(this._component_entidad + '/factor/' + this.item_factor_eliminar.id)
      .subscribe(
        (data: any) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'success', dismissible: true, istimeout: true, timeout:10000, msg: 'Se eliminó el factor con éxito'})),
        (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error al eliminar el factor'}))
      );
  }

  ngOnInit() {
    this._component_entidad_id = this.route.snapshot.params['id'];
    this.getListas();
  }
}
