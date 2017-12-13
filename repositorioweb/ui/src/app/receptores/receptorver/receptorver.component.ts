import { Component, OnInit, TemplateRef } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Receptor, ReceptorMascara } from '../../models/receptor.model';
import { Ministerio } from '../../models/ministerio.model';
import { Subsecretaria } from '../../models/subsecretaria.model';
import { Institucion } from '../../models/institucion.model';
import { MantenedorItem } from '../../models/mantenedor.model';
import { BsModalService } from 'ngx-bootstrap/modal';
import { BsModalRef } from 'ngx-bootstrap/modal/modal-options.class';
import { ApiService } from '../../services/api.service';
import { AlertasService } from '../../services/alertas.service';
import { Alerta } from '../../models/alerta.model';

@Component({
  selector: 'app-receptorver',
  templateUrl: './receptorver.component.html',
  styleUrls: ['./receptorver.component.css']
})
export class ReceptorverComponent implements OnInit {

  public _component_entidad: string = 'receptor';
  public _component_entidad_id: number;

  public _lista_canales: MantenedorItem[] = [];
  public _lista_formatos: MantenedorItem[] = [];
  public _lista_plantillas: MantenedorItem[] = [];
  public _lista_periodicidades: MantenedorItem[] = [];
  public _lista_ministerios: Ministerio[] = [];
  public _lista_subsecretarias: Subsecretaria[] = [];
  public _lista_instituciones: Institucion[] = [];
  public item: Receptor = new Receptor({});
  public item_mascaras: ReceptorMascara[] = [];
  public item_mascara_eliminar: ReceptorMascara;
  public modalRef: BsModalRef;

  constructor(private _apiService: ApiService, private _alertasService: AlertasService, private router: Router, private route: ActivatedRoute, private modalService: BsModalService) { }

  getListas() {
    this._apiService.getLista('mantenedor/canal_transmision')
      .subscribe(
        (data: any[]) => {
          this._lista_canales = [];
          if ( data.length > 0 ) {
            for (const d of data) {
              this._lista_canales.push( new MantenedorItem(d) );
            }
          }
        },
        (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error al cargar lista de canales de transmisión'})),
        () => this._apiService.getLista('mantenedor/formato')
          .subscribe(
            (data: any[]) => {
              this._lista_formatos = [];
              if ( data.length > 0 ) {
                for (const d of data) {
                  this._lista_formatos.push( new MantenedorItem(d) );
                }
              }
            },
            (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error al cargar lista de formatos'})),
            () => this._apiService.getLista('mantenedor/plantilla_recepcion')
              .subscribe(
                (data: any[]) => {
                  this._lista_plantillas = [];
                  if ( data.length > 0 ) {
                    for (const d of data) {
                      this._lista_plantillas.push( new MantenedorItem(d) );
                    }
                  }
                },
                (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error al cargar lista de plantillas'})),
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
                    () => this.getListasMinisteriales()
                  )
              )
          )
      );
  }

  getListasMinisteriales() {
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
    if ( typeof this._component_entidad_id !== 'undefined' && this._component_entidad_id !== null && this._component_entidad_id !== 0 ) {
      this._apiService.load(this._component_entidad, this._component_entidad_id)
        .subscribe(
          (data: any) => {
            this.item = new Receptor(data.pop());
          },
          (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error al cargar el receptor ID: ' + this._component_entidad_id})),
          () => this._apiService.getLista(this._component_entidad + '/mascara/' + this._component_entidad_id, {orden: 'receptor_id'})
            .subscribe(
              (data: any[]) => {
                this.item_mascaras = [];
                if ( data.length > 0 ) {
                  for (const d of data) {
                    this.item_mascaras.push( new ReceptorMascara(d) );
                  }
                }
              },
              (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error al buscar los permisos jerárquicos del receptor'}))
            )
        );
    }
  }

  onEliminar (template: TemplateRef<any>) {
    this.modalRef = this.modalService.show(template);
  }

  _onEliminar () {
    this.modalRef.hide();
    this._apiService.borrar(this._component_entidad + '/' + this._component_entidad_id)
      .subscribe(
        (data: any) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'success', dismissible: true, istimeout: true, timeout:10000, msg: 'Se eliminó el receptor "' + this.item.nombre + '" con éxito'})),
        (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error al eliminar el receptor "' + this.item.nombre + '"'})),
        () => {
          this.router.navigate(['/receptores']);
          this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'success', dismissible: true, istimeout: true, timeout:10000, msg: 'Se eliminó el receptor "' + this.item.nombre + '" con éxito'}));
        }
      );
  }

  onAgregarMascara () {
    this.router.navigate(['/receptores', Number(this._component_entidad_id), 'mascara', 0], );
  }

  onEditarMascara (idMascara: number) {
    this.router.navigate(['/receptores', Number(this._component_entidad_id), 'mascara', idMascara], );
  }

  onEliminarMascara (template: TemplateRef<any>, factor: ReceptorMascara) {
    this.item_mascara_eliminar = factor;
    this.modalRef = this.modalService.show(template);
  }

  _onEliminarMascara () {
    this.modalRef.hide();
    this._apiService.borrar(this._component_entidad + '/mascara/' + this.item_mascara_eliminar.id)
      .subscribe(
        (data: any) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'success', dismissible: true, istimeout: true, timeout:10000, msg: 'Se eliminó la máscara con éxito'})),
        (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error al eliminar la máscara'}))
      );
  }

  ngOnInit() {
    this._component_entidad_id = this.route.snapshot.params['id'];
    this.getListas();
  }
}
