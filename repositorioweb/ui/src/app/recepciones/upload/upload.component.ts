import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import {ApiService} from "../../services/api.service";
import {AlertasService} from "../../services/alertas.service";
import {Alerta} from "../../models/alerta.model";


@Component({
  selector: 'app-upload',
  templateUrl: './upload.component.html',
  providers: []
})
export class UploadComponent implements OnInit {

  urlBase: string;
  _item = '';
  _items: any[] = [];

  constructor(private router: Router, private _apiService: ApiService, private _alertasService: AlertasService){
    this.urlBase = '';
  }

  getListas() {
    this._apiService.getLista('recepcion/receptores')
      .subscribe(
        (data: any[]) => {
          console.log(data);
          this._items = [];
          if ( data.length > 0 ) {
            for (const d of data) {
              this._items.push( {id: d.id, nombre: d.nombre});
            }
          }
        },
        (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error al cargar lista de receptores'}))
      );
  }

  onCancelar(){
    this.router.navigate(['/recepciones']);
  }

  ngOnInit(){
    this.getListas();
  }
}
