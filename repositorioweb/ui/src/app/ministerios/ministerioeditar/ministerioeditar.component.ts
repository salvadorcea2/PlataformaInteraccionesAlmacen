import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Ministerio } from '../../models/ministerio.model';
import { NgForm } from '@angular/forms';
import { BsModalRef } from 'ngx-bootstrap';
import { ApiService } from '../../services/api.service';
import { AlertasService } from '../../services/alertas.service';
import { Alerta } from '../../models/alerta.model';

@Component({
  selector: 'app-ministerioeditar',
  templateUrl: './ministerioeditar.component.html',
  styleUrls: ['./ministerioeditar.component.css']
})
export class MinisterioeditarComponent implements OnInit {
  public _component_entidad: string = 'ministerio';
  public _component_entidad_id: number;

  public item: Ministerio = new Ministerio({});
  public modalRef: BsModalRef;

  constructor(private _apiService: ApiService, private _alertasService: AlertasService, private router: Router, private route: ActivatedRoute) { }

  updateItem() {
    if ( typeof this._component_entidad_id !== 'undefined' && this._component_entidad_id !== null ) {
      this._apiService.load(this._component_entidad, this._component_entidad_id)
        .subscribe(
          (data: any) => {
            this.item = new Ministerio(data.pop());
          },
          (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error al cargar el ministerio ID: ' + this._component_entidad_id}))
        );
    }
  }

  onSubmit(frm: NgForm){
    if ( !frm.valid ) {
      this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error en el formulario, favor revisa nuevamente'}));
      return;
    }
    if ( typeof this._component_entidad_id === 'undefined' || this._component_entidad_id === null ) {
      this._apiService.crear(this._component_entidad, this.item)
        .subscribe(
          (data: any) => {
            this._component_entidad_id = Number(data[0].id);
          },
          (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: error.text()})),
          () => this.router.navigate(['/ministerios', Number(this._component_entidad_id), 'ver'])
        );
    }else {
      this._apiService.actualizar(this._component_entidad, this.item)
        .subscribe(
          (data: any) => {
            this._component_entidad_id = Number(data[0].id);
          },
          (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: error.text()})),
          () => this.router.navigate(['/ministerios', Number(this._component_entidad_id), 'ver'])
        );
    }
  }

  ngOnInit() {
    this._component_entidad_id = this.route.snapshot.params['id'];
    // if ( typeof this._component_entidad_id === 'undefined' || this._component_entidad_id === null ) {
    //    this._component_entidad_id = 0;
    // }
    this.updateItem();
  }
}
