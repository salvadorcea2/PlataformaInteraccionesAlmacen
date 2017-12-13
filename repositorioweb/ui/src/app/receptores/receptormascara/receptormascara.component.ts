import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Event, Router } from '@angular/router';
import { Ministerio } from '../../models/ministerio.model';
import { Subsecretaria } from '../../models/subsecretaria.model';
import { Institucion } from '../../models/institucion.model';
import { Receptor, ReceptorMascara } from '../../models/receptor.model';
import { ApiService } from '../../services/api.service';
import { AlertasService } from '../../services/alertas.service';
import { Alerta } from '../../models/alerta.model';
import { NgForm } from '@angular/forms';

@Component({
  selector: 'app-receptormascara',
  templateUrl: './receptormascara.component.html',
  styleUrls: ['./receptormascara.component.css']
})
export class ReceptormascaraComponent implements OnInit {

  public _component_entidad: string = 'receptor';
  public _component_entidad_id: number;
  public _component_mascara_id: number;

  public _lista_ministerios: Ministerio[] = [];
  public _lista_subsecretarias: Subsecretaria[] = [];
  public _lista_instituciones: Institucion[] = [];
  public parent: Receptor = new Receptor({});
  public item: ReceptorMascara = new ReceptorMascara({});
  public item_mascaras: ReceptorMascara[] = [];

  constructor(private _apiService: ApiService, private _alertasService: AlertasService, private router: Router, private route: ActivatedRoute) { }

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
    if ( typeof this._component_entidad_id !== 'undefined' && this._component_entidad_id !== null && this._component_entidad_id !== 0 ) {
      this._apiService.load(this._component_entidad, this._component_entidad_id)
        .subscribe(
          (data: any) => {
            this.parent = new Receptor(data.pop());
          },
          (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error al cargar el receptor ID: ' + this._component_entidad_id})),
          () => this._apiService.getLista(this._component_entidad + '/mascara/' + this._component_entidad_id, {orden: 'receptor_id'})
            .subscribe(
              (data: any[]) => {
                this.item_mascaras = [];
                if ( data.length > 0 ) {
                  for (const d of data) {
                    this.item_mascaras.push( new ReceptorMascara(d) );
                    if( Number(d.id) === Number(this._component_mascara_id) ) {
                      this.item = new ReceptorMascara(d);
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

    this.item.receptor_id = +this._component_entidad_id;
    this.item.ministerio_id = +this.item.ministerio_id;
    this.item.subsecretaria_id = +this.item.subsecretaria_id;
    this.item.institucion_id = +this.item.institucion_id;

    if ( typeof this._component_mascara_id === 'undefined' || this._component_mascara_id === null || this._component_mascara_id === 0 ) {
      this._apiService.crear(this._component_entidad + '/mascara', this.item)
        .subscribe(
          (data: any) => {
            this._component_mascara_id = Number(data[0].id);
          },
          (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: error.text()})),
          () => this.router.navigate(['/receptores', Number(this._component_entidad_id), 'ver'])
        );
    }else {
      this._apiService.actualizar(this._component_entidad + '/mascara', this.item)
        .subscribe(
          (data: any) => {
            this._component_mascara_id = Number(data[0].id);
          },
          (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: error.text()})),
          () => this.router.navigate(['/receptores', Number(this._component_entidad_id), 'ver'])
        );
    }
  }

  ngOnInit() {
    this._component_entidad_id = +this.route.snapshot.params['id'];
    this._component_mascara_id = +this.route.snapshot.params['mascara'];
    this.getListas();
  }

}
