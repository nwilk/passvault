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

#import "JNKeychainPasswordStore.h"
#import "JNKeychain.h"


@implementation JNKeychainPasswordStore {

}
- (NSString *)loadFromKey:(NSString *)key {
    return [JNKeychain loadValueForKey:key];
}

- (void)saveValue:(NSString *)value withKey:(NSString *)key {
    [JNKeychain saveValue:value forKey:key];
}

- (void)saveIntValue:(NSInteger)value withKey:(NSString *)key {
    [JNKeychain saveValue:@(value) forKey:key];
}

- (NSInteger)loadIntFromKey:(NSString*)key {
    return ((NSNumber *)[JNKeychain loadValueForKey:key]).integerValue;
}

- (void)deleteValueWithKey:(NSString *)key {
    [JNKeychain deleteValueForKey:key];
}

@end