import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import { Buscador } from '../../models/buscador.model';

@Component({
  selector: 'app-paginador',
  templateUrl: './paginador.component.html',
  styleUrls: ['./paginador.component.css']
})
export class PaginadorComponent implements OnInit {

  @Input() public _buscador: Buscador = null;
  @Output() paginadorCambia = new EventEmitter<Buscador>();

  constructor() { }

  onPaginacionPaginaCambia (p: number) {
    this._buscador.paginador.pagina = p;
    this._buscador.paginador.inicio = ( this._buscador.paginador.pagina - 1 ) * this._buscador.paginador.porpagina;
    this.paginadorCambia.emit(this._buscador);
  }

  onPorPaginaCambia () {
    this.paginadorCambia.emit(this._buscador);
  }

  ngOnInit() {
  }

}
