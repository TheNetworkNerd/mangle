import {Component, OnInit} from "@angular/core";
import {RequestsService} from "../requests.service";
import {Router} from "@angular/router";
import {ClrDatagridSortOrder} from "@clr/angular";
import {MessageConstants} from "src/app/common/message.constants";

@Component({
  selector: "app-scheduled",
  templateUrl: "./scheduled.component.html"
})
export class ScheduledComponent implements OnInit {

  constructor(private requestsService: RequestsService) {

  }

  public alertMessage: string;
  public isErrorMessage: boolean;
  public viewReportFlag: boolean;
  public viewDetailedStatus: boolean;
  public modifySchedule: boolean;
  public notifierModal: boolean;

  public scheduledJobs: any = [];
  public scheduledTaskDetails: any = {};

  public isLoadingScheduledJobs = false;
  public isLoadingScheduledTaskDetails = false;

  public startTimeDesc = ClrDatagridSortOrder.DESC;

  public jobType: any = ["CRON", "SIMPLE"];
  public status: any = ["INITIALIZING", "CANCELLED", "SCHEDULED", "FINISHED", "PAUSED", "SCHEDULE_FAILED"];
  public cronModal: boolean = false;
  public notifiersData: any = [];

  public schedulerFormData: any = {
    "id": null,
    "jobType": null,
    "scheduledTime": null,
    "cronExpression": null,
    "status": null,
    "description": null
  };

  ngOnInit() {
    this.getAllScheduleJobs();
  }

  public getAllScheduleJobs() {
    this.isLoadingScheduledJobs = true;
    this.requestsService.getAllScheduleJobs().subscribe(
      res => {
        if (res.code) {
          this.scheduledJobs = [];
          this.isLoadingScheduledJobs = false;
        } else {
          this.scheduledJobs = res.content;
          this.isLoadingScheduledJobs = false;
        }
      }, err => {
        this.scheduledJobs = [];
        this.isErrorMessage = true;
        this.alertMessage = err.error.description;
        this.isLoadingScheduledJobs = false;
      }
    );
  }

  public deleteScheduleOnly(scheduleTask) {
    if (confirm(MessageConstants.DELETE_CONFIRM + scheduleTask.id + MessageConstants.QUESTION_MARK)) {
      this.requestsService.deleteScheduleOnly(scheduleTask.id).subscribe(
        res => {
          this.getAllScheduleJobs();
          this.isErrorMessage = false;
          this.alertMessage = scheduleTask.id + MessageConstants.SCHEDULE_DELETE;
          this.isLoadingScheduledJobs = false;
        }, err => {
          this.getAllScheduleJobs();
          this.isErrorMessage = true;
          this.alertMessage = err.error.description;
          this.isLoadingScheduledJobs = false;
          if (this.alertMessage === undefined) {
            this.isErrorMessage = true;
            this.alertMessage = err.error.error;
          }
        });
    } else {
      // Do nothing!
    }
  }

  public deleteSchedule(scheduleTask) {
    if (confirm(MessageConstants.DELETE_SCHEDULE_CONFIRM + scheduleTask.id + MessageConstants.QUESTION_MARK)) {
      this.requestsService.deleteSchedule(scheduleTask.id).subscribe(
        res => {
          this.getAllScheduleJobs();
          this.isErrorMessage = false;
          this.alertMessage = scheduleTask.id + MessageConstants.SCHEDULE_DELETE;
          this.isLoadingScheduledJobs = false;
        }, err => {
          this.getAllScheduleJobs();
          this.isErrorMessage = true;
          this.alertMessage = err.error.description;
          this.isLoadingScheduledJobs = false;
          if (this.alertMessage === undefined) {
            this.isErrorMessage = true;
            this.alertMessage = err.error.error;
          }
        });
    } else {
      // Do nothing!
    }
  }

  public cancelSchedule(scheduleTask) {
    if (confirm(MessageConstants.CANCEL_CONFIRM + scheduleTask.id + MessageConstants.QUESTION_MARK)) {
      this.requestsService.cancelSchedule(scheduleTask.id).subscribe(
        res => {
          this.getAllScheduleJobs();
          this.isErrorMessage = false;
          this.alertMessage = scheduleTask.id + MessageConstants.SCHEDULE_CANCEL;
          this.isLoadingScheduledJobs = false;
        }, err => {
          this.getAllScheduleJobs();
          this.isErrorMessage = true;
          this.alertMessage = err.error.description;
          this.isLoadingScheduledJobs = false;
          if (this.alertMessage === undefined) {
            this.isErrorMessage = true;
            this.alertMessage = err.error.error;
          }
        });
    } else {
      // Do nothing!
    }
  }

  public pauseSchedule(scheduleTask) {
    if (confirm(MessageConstants.PAUSE_CONFIRM + scheduleTask.id + MessageConstants.QUESTION_MARK)) {
      this.requestsService.pauseSchedule(scheduleTask.id).subscribe(
        res => {
          this.getAllScheduleJobs();
          this.isErrorMessage = false;
          this.alertMessage = scheduleTask.id + MessageConstants.SCHEDULE_PAUSE;
          this.isLoadingScheduledJobs = false;
        }, err => {
          this.getAllScheduleJobs();
          this.isErrorMessage = true;
          this.alertMessage = err.error.description;
          this.isLoadingScheduledJobs = false;
          if (this.alertMessage === undefined) {
            this.isErrorMessage = true;
            this.alertMessage = err.error.error;
          }
        });
    } else {
      // Do nothing!
    }
  }

  public populateScheduler(scheduledJob) {
    this.schedulerFormData = scheduledJob;
    this.populateFaultNotifiers(scheduledJob);
  }

  public modifyScheduleData(scheduleTask) {
    this.addNotifiersInScheduleTask(scheduleTask);
    if (confirm(MessageConstants.MODIFY_CONFIRM + scheduleTask.id + MessageConstants.QUESTION_MARK)) {
      this.isLoadingScheduledJobs = true;
      this.requestsService.modifySchedule(scheduleTask).subscribe(
        res => {
          this.getAllScheduleJobs();
          this.isErrorMessage = false;
          this.alertMessage = scheduleTask.id + MessageConstants.SCHEDULE_MODIFY;
          this.isLoadingScheduledJobs = false;
        }, err => {
          this.getAllScheduleJobs();
          this.isErrorMessage = true;
          this.alertMessage = err.error.description;
          this.isLoadingScheduledJobs = false;
          if (this.alertMessage === undefined) {
            this.isErrorMessage = true;
            this.alertMessage = err.error.error;
          }
        });
    }
  }

  public setScheduleCron(eventVal) {
    this.schedulerFormData.cronExpression = eventVal;
    this.cronModal = false;
  }

  public resumeSchedule(scheduleTask) {
    if (confirm(MessageConstants.RESUME_CONFIRM + scheduleTask.id + MessageConstants.QUESTION_MARK)) {
      this.requestsService.resumeSchedule(scheduleTask.id).subscribe(
        res => {
          this.getAllScheduleJobs();
          this.isErrorMessage = false;
          this.alertMessage = scheduleTask.id + MessageConstants.SCHEDULE_RESUME;
          this.isLoadingScheduledJobs = false;
        }, err => {
          this.getAllScheduleJobs();
          this.isErrorMessage = true;
          this.alertMessage = err.error.description;
          this.isLoadingScheduledJobs = false;
          if (this.alertMessage === undefined) {
            this.alertMessage = err.error.error;
          }
        });
    } else {
      // Do nothing!
    }
  }

  public getScheduledTaskDetails(scheduleTask) {
    this.isLoadingScheduledTaskDetails = true;
    this.requestsService.getTaskById(scheduleTask.id).subscribe(
      res => {
        if (res.code) {
          this.isErrorMessage = true;
          this.alertMessage = res.description;
          this.isLoadingScheduledTaskDetails = false;
        } else {
          this.scheduledTaskDetails = res;
          if (res.triggers != null) {
            for (var i = 0; i < res.triggers.length; i++) {
              res.triggers[i].startTime = new Date(res.triggers[i].startTime);
              if (res.triggers[i].endTime != null) {
                res.triggers[i].endTime = new Date(res.triggers[i].endTime);
              }
            }
          }
          this.isLoadingScheduledTaskDetails = false;
        }
      }, err => {
        this.isErrorMessage = true;
        this.alertMessage = err.error.description;
        this.isLoadingScheduledTaskDetails = false;
      }
    );
  }

  public populateFaultNotifiers(scheduledJob: any) {
    if (scheduledJob.notifierNames != null && scheduledJob.notifierNames.length > 0) {
      this.notifiersData = scheduledJob.notifierNames;
    }
  }

  public addNotifiersInScheduleTask(scheduleTask: any) {
    if (this.notifiersData !== [] && this.notifiersData.length > 0) {
      scheduleTask.notifierNames = this.notifiersData;
    }
  }
}
