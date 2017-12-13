import { Component, OnInit } from '@angular/core';
import { Alerta } from '../models/alerta.model';
import { AlertasService } from '../services/alertas.service';

@Component({
  selector: 'app-alerts',
  templateUrl: './alerts.component.html',
  styleUrls: ['./alerts.component.css']
})
export class AlertsComponent implements OnInit {

  public alerts: Alerta[] = [];
  constructor(private _alertasService: AlertasService) {}

  ngOnInit() {
    // success info warning danger
    this._alertasService.onNuevaAlerta
      .subscribe(
        (alerta: Alerta) => this.alerts.push(alerta)
      );
  }

}
