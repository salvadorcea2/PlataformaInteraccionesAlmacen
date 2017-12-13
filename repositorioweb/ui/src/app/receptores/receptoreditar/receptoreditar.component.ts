import {Component, OnInit, TemplateRef} from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { NgForm } from '@angular/forms';
import {Receptor, ReceptorMascara} from '../../models/receptor.model';
import { MantenedorItem } from '../../models/mantenedor.model';
import {Alerta} from '../../models/alerta.model';
import {BsModalRef, BsModalService} from 'ngx-bootstrap';
import {ApiService} from '../../services/api.service';
import {AlertasService} from '../../services/alertas.service';
import {FromlistaPipe} from '../../pipes/fromlista.pipe';
import {Ministerio} from '../../models/ministerio.model';
import {Subsecretaria} from '../../models/subsecretaria.model';
import {Institucion} from '../../models/institucion.model';

@Component({
  selector: 'app-receptoreditar',
  templateUrl: './receptoreditar.component.html',
  styleUrls: ['./receptoreditar.component.css']
})
export class ReceptoreditarComponent implements OnInit {

  public _component_entidad: string = 'receptor';
  public _component_entidad_id: number;

  public _lista_ministerios: Ministerio[] = [];
  public _lista_subsecretarias: Subsecretaria[] = [];
  public _lista_instituciones: Institucion[] = [];
  public _lista_canales: MantenedorItem[] = [];
  public _lista_formatos: MantenedorItem[] = [];
  public _lista_plantillas: MantenedorItem[] = [];
  public _lista_periodicidades: MantenedorItem[] = [];
  public _lista_tipo_interacciones: MantenedorItem[] = [];
  public item: Receptor = new Receptor({});
  public item_mascaras: ReceptorMascara[] = [];
  public item_mascara_eliminar: ReceptorMascara;
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
                () => this._apiService.getLista('mantenedor/canal_transmision')
                  .subscribe(
                    (data: any[]) => {
                      this._lista_canales = [];
                      if ( data.length > 0 ) {
                        for (const d of data) {
                          this._lista_canales.push( new MantenedorItem(d) );
                        }
                      }
                    },
                    (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error al cargar lista de canales'})),
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
    if ( typeof this._component_entidad_id !== 'undefined' && this._component_entidad_id !== null ) {
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
              (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error al buscar las máscaras del receptor'}))
            )
        );
    }
  }

  onSubmit(frm: NgForm){
    if ( !frm.valid ) {
      this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error en el formulario, favor revisa nuevamente'}));
      return;
    }

    this.item.canal_transmision_id = +this.item.canal_transmision_id;
    this.item.formato_id = +this.item.formato_id;
    this.item.plantilla_recepcion_id = +this.item.plantilla_recepcion_id;
    this.item.periodicidad_id = +this.item.periodicidad_id;

    if ( typeof this._component_entidad_id === 'undefined' || this._component_entidad_id === null ) {
      this._apiService.crear(this._component_entidad, this.item)
        .subscribe(
          (data: any) => {
            this._component_entidad_id = Number(data[0].id);
          },
          (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: error.text()})),
          () => this.router.navigate(['/receptores', Number(this._component_entidad_id), 'ver'])
        );
    }else {
      this._apiService.actualizar(this._component_entidad, this.item)
        .subscribe(
          (data: any) => {
            this._component_entidad_id = Number(data[0].id);
          },
          (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: error.text()})),
          () => this.router.navigate(['/receptores', Number(this._component_entidad_id), 'ver'])
        );
    }
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
/*  id: number = 0;
  canales: MantenedorItem[] = [];
  formatos: MantenedorItem[] = [];
  plantillas: MantenedorItem[] = [];
  periodicidades: MantenedorItem[] = [];
  propiedades: any[] = [];
  item: Receptor = new Receptor({});

  constructor(private router: Router, private route: ActivatedRoute, private backendService: BackendService) { }

  getData() {
    this.backendService.getLista('mantenedor/canal_transmision')
      .subscribe(
        (response: Response) => {
          const data = response.json();
          this.canales = [];
          if( data.data.length   > 0 ) {
            for (const d of data.data) {
              this.canales.push( new MantenedorItem(d) );
            }
          }
        },
        (error) => console.log(error),
        () => this.backendService.getLista('mantenedor/formato')
          .subscribe(
            (response: Response) => {
              const data = response.json();
              this.formatos = [];
              if( data.data.length > 0 ) {
                for (const d of data.data) {
                  this.formatos.push( new MantenedorItem(d) );
                }
              }
            },
            (error) => console.log(error),
            () => this.backendService.getLista('mantenedor/plantilla_recepcion')
              .subscribe(
                (response: Response) => {
                  const data = response.json();
                  this.plantillas = [];
                  if( data.data.length > 0 ) {
                    for (const d of data.data) {
                      this.plantillas.push( new MantenedorItem(d) );
                    }
                  }
                },
                (error) => console.log(error),
                () => this.backendService.getLista('mantenedor/periodicidad')
                  .subscribe(
                    (response: Response) => {
                      const data = response.json();
                      this.periodicidades = [];
                      if( data.data.length > 0 ) {
                        for (const d of data.data) {
                          this.periodicidades.push( new MantenedorItem(d) );
                        }
                      }
                    },
                    (error) => console.log(error),
                    () => this.getReceptor()
                  )
              )
          )
      );
  }

  getReceptor() {
    if ( typeof this.id !== 'undefined' && this.id !== null && this.id !== 0 ) {
      this.backendService.getReceptor(this.id)
        .subscribe(
          (response: Response) => {
            const data = response.json().data.pop();
            console.log(data);
            this.item = new Receptor(data);
          },
          (error) => console.log(error)
        );
    }
  }

  onSubmit(frm: NgForm){
    console.log('onSubmit');
    console.log(frm);
    if ( frm.valid ) {
      this.backendService.update('receptor', {
        id: Number(this.id),
        nombre: this.item.nombre,
        descripcion: this.item.descripcion,
        canal_transmision_id: Number(this.item.canal_transmision_id),
        formato_id: Number(this.item.formato_id),
        plantilla_recepcion_id: Number(this.item.plantilla_recepcion_id),
        periodicidad_id: Number(this.item.periodicidad_id),
        notificacion_diaria: this.item.notificacion_diaria,
        propiedades: this.item.propiedades,
        habilitado: this.item.habilitado
      })
        .subscribe(
          (response: Response) => {
            const data = response.json();
            console.log(data);
          },
          (error) => console.log(error),
          () => this.router.navigate(['/receptores', Number(this.id), 'ver'])
        );
    }
  }

  ngOnInit() {
    this.id = this.route.snapshot.params['id'];
    if ( typeof this.id === 'undefined' || this.id === null ) {
      this.id = 0;
    }
    this.getData();
  }
*/
}
