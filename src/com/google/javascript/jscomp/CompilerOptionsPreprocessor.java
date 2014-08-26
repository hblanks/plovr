/*
 * Copyright 2014 The Closure Compiler Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.javascript.jscomp;

/**
 * Checks for combinations of options that are incompatible, i.e. will produce
 * incorrect code.
 *
 * Also, turns off options if the provided options don't make sense together.
 *
 * @author tbreisacher@google.com (Tyler Breisacher)
 */
final class CompilerOptionsPreprocessor {

  static void preprocess(CompilerOptions options) {
    if (options.checkMissingGetCssNameLevel.isOn()
        && (options.checkMissingGetCssNameBlacklist == null
            || options.checkMissingGetCssNameBlacklist.isEmpty())) {
      throw new InvalidOptionsException(
          "Cannot check use of goog.getCssName because of empty blacklist.");
    }

    if (options.getLanguageIn() == options.getLanguageOut()) {
      // No conversion.
    } else if (!options.getLanguageIn().isEs6OrHigher() ||
        options.getLanguageOut() != CompilerOptions.LanguageMode.ECMASCRIPT3) {
      throw new InvalidOptionsException(
          "Can only convert code from ES6 to ES3. "
          + "Cannot convert from %s to %s.",
          options.getLanguageIn(), options.getLanguageOut());
    }

    if (options.useNewTypeInference) {
      options.checkTypes = false;
      options.inferTypes = false;
      options.checkMissingReturn = CheckLevel.OFF;
      options.checkGlobalThisLevel = CheckLevel.OFF;
      // There is also overlap in the warnings of GlobalTypeInfo and VarCheck
      // and VariableReferenceCheck.
      // But VarCheck is always added in DefaultPassConfig, and
      // VariableReferenceCheck finds warnings that we don't, so leave them on.
    }
  }

  /**
   * Exception to indicate incompatible options in the CompilerOptions.
   */
  public static class InvalidOptionsException extends RuntimeException {
    private InvalidOptionsException(String message, Object... args) {
      super(String.format(message, args));
    }
  }

  // Don't instantiate.
  private CompilerOptionsPreprocessor() {
  }
}
