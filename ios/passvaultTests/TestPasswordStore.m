// Copyright 2014 Neil Wilkinson
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//        http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

#import "TestPasswordStore.h"


@implementation TestPasswordStore {

    NSMutableDictionary *passwords;
}

- (id)init {
    self = [super init];
    if (self) {
        passwords = [[NSMutableDictionary alloc] init];
    }

    return self;
}

- (NSString *)loadFromKey:(NSString *)key {
    return passwords[key];
}

- (void)saveValue:(NSString *)value withKey:(NSString *)key {
    [passwords setValue:value forKey:key];
}

- (void)deleteValueWithKey:(NSString *)key {
    [passwords removeObjectForKey:key];
}

- (void)saveIntValue:(NSInteger)value withKey:(NSString *)key {

}

- (NSInteger)loadIntFromKey:(NSString *)key {
    return 0;
}


@end