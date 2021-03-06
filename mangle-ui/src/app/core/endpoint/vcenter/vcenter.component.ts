import {Component, OnInit} from "@angular/core";
import {EndpointService} from "../endpoint.service";
import {CommonEndpoint} from "../common.endpoint";
import {CommonConstants} from "src/app/common/common.constants";

@Component({
  selector: "app-vcenter",
  templateUrl: "./vcenter.component.html"
})
export class VcenterComponent extends CommonEndpoint implements OnInit {

  constructor(endpointService: EndpointService) {
    super(endpointService, CommonConstants.VCENTER);
  }

  ngOnInit() {
    this.getEndpoints();
    this.getCredentials();
    this.getVCenterAdapters();
  }

}
