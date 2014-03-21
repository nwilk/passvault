//
// Created by Neil on 26/02/2014.
// Copyright (c) 2014 MeasuredSoftware. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "PasswordViewController.h"

@class TouchZone;
@class PasswordField;
@class PasswordList;

@interface AddPasswordViewController : PasswordViewController

@property(nonatomic, strong) IBOutlet TouchZone *touchZone;
@property(nonatomic, strong) IBOutlet UILabel *passwordLengthText;
@property(nonatomic, strong) IBOutlet UISlider *passwordLengthSlider;

- (void)configureWithPasswordList:(PasswordList *)list;

- (IBAction)lengthChanged;

@end